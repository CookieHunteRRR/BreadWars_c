package me.cookiehunterrr.breadwars.listeners;

import me.cookiehunterrr.breadwars.classes.Constants;
import me.cookiehunterrr.breadwars.classes.Utils;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.customitems.CustomAttribute;
import me.cookiehunterrr.breadwars.classes.gamesession.GameSession;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class PlayerDeathListener implements Listener
{
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e)
    {
        Player victim = e.getEntity().getPlayer();
        if (victim == null) return;
        PlayerInfo victimPlayerInfo = playerInfoManager.getPlayerInfo(victim.getUniqueId());
        GameSession session = victimPlayerInfo.getCrew().getCurrentGameSession();
        try
        {
            if (session.getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        NamespacedKey soulboundKey = CustomAttribute.SOULBOUND.getAsNamespacedKey();
        NamespacedKey fixedSlotKey = CustomAttribute.FIXED_SLOT.getAsNamespacedKey();
        ArrayList<ItemStack> itemsToRetain = new ArrayList<>();
        // Проходимся по броне и убираем ее из пула дропов, если она соулбаунд
        // Если она не соулбаунд, она отправится во второй метод, где опять останется дропом
        for (ItemStack item : victim.getInventory().getArmorContents())
        {
            if (item == null) continue;
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            if (!dataContainer.has(soulboundKey, PersistentDataType.INTEGER)) continue;
            int appropriateSlot = Utils.getAppropriateSlotIndex(item.getType().getEquipmentSlot());
            dataContainer.set(fixedSlotKey, PersistentDataType.INTEGER, appropriateSlot);
            item.setItemMeta(meta);
            itemsToRetain.add(item);
            e.getDrops().remove(item);
        }
        // Отсортировав соулбаунд броню, создаем копию дропов
        ArrayList<ItemStack> cloneOfDrops = new ArrayList<>(e.getDrops());
        // Проходимся по всем остальным предметам, соулбаунд предметы передаем в itemsToRetain
        for (ItemStack drop : cloneOfDrops)
        {
            PersistentDataContainer itemData = drop.getItemMeta().getPersistentDataContainer();
            if (!itemData.has(soulboundKey, PersistentDataType.INTEGER)) continue;
            itemsToRetain.add(drop);
            e.getDrops().remove(drop);
        }
        victimPlayerInfo.setSavedItemsOnDeath(itemsToRetain);

        Player killer = e.getEntity().getKiller();
        SessionCrewManager crewManager = session.getCrewManager();
        Crew enemyCrew = crewManager.getOppositeCrew(victimPlayerInfo.getCrew());
        if (killer == null)
        {
            if (crewManager.getCrewFlagOwners(enemyCrew).contains(victim))
            {
                crewManager.changeFlagOwner(enemyCrew, victim, crewManager.getPlayerAbleToCarryAllyFlag(enemyCrew));
                victim.sendMessage("§3Вы потеряли вражеский флаг");
                session.getScoreboardManager().updateScoreboardsForPlayers();
            }
            return;
        }

        PlayerInfo killerPlayerInfo = playerInfoManager.getPlayerInfo(killer.getUniqueId());
        // Проверка на то, находится ли убийца игрока во вражеской команде
        if (crewManager.getOppositeCrew(victimPlayerInfo.getCrew()) != enemyCrew) return;
        crewManager.addCrewScore(enemyCrew, Constants.scorePerKill);
        // Проверка на наличие у жертвы (условная команда Б) флага команды убийцы (команда А)
        // (то есть может ли убийца вернуть флаг своей команды)
        if (crewManager.getCrewFlagOwners(enemyCrew).contains(victim))
        {
            // Может ли убийца (команда А) нести флаг своей команды А
            if (crewManager.isAbleToCarryFlagType(killer, true))
            {
                crewManager.changeFlagOwner(enemyCrew, victim, killer);
                victim.sendMessage("§3Вы потеряли флаг вражеской команды");
                killer.sendMessage("§3Вы вернули флаг вашей команды, убив §4" + victim.getDisplayName());
            }
            // Метод поиска игрока которому возможно передать этот флаг
            else
            {
                Player newFlagOwner = crewManager.getPlayerAbleToCarryAllyFlag(enemyCrew);
                crewManager.changeFlagOwner(enemyCrew, victim, newFlagOwner);
                victim.sendMessage("§3Вы потеряли флаг вражеской команды");
                newFlagOwner.sendMessage("§3Вы получили флаг вашей команды, т.к. §a" + killer.getDisplayName() +
                        "§3 не имел места под флаг при убийстве §4" + victim.getDisplayName());
            }
            //if (BreadWars.isDebugMode()) Debug.showFlagOwners(killer, session);
        }
        // Проверка на наличие у жертвы (команда Б) флага команды А
        if (crewManager.getCrewFlagOwners(victimPlayerInfo.getCrew()).contains(victim))
        {
            // Может ли убийца (А) нести флаг вражеской команды (Б)
            // Если не может, флаг остается у врага
            if (crewManager.isAbleToCarryFlagType(killer, false))
            {
                crewManager.changeFlagOwner(victimPlayerInfo.getCrew(), victim, killer);
                victim.sendMessage("§3Вы потеряли флаг своей команды");
                killer.sendMessage("§3Вы получили вражеский флаг, убив §4" + victim.getDisplayName());
                //if (BreadWars.isDebugMode()) Debug.showFlagOwners(killer, session);
            }
        }
        // Обновляем скорборды, так как явно что-то поменялось
        session.getScoreboardManager().updateScoreboardsForPlayers();
    }
}
