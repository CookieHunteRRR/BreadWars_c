package me.cookiehunterrr.breadwars.classes.tracker;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.customitems.CustomAttribute;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class TrackerManager
{
    final String trackerItemName = "§2Трекер";
    final public static int trackerReservedInventorySlot = 7;
    Map<Player, Player> targetMap;
    ArrayList<Player> currentlyHolding;
    SessionCrewManager crewManager;

    public TrackerManager(SessionCrewManager crewManager)
    {
        targetMap = new HashMap<>();
        currentlyHolding = new ArrayList<>();
        this.crewManager = crewManager;
    }

    public void addToCurrentlyHolding(Player player)
    {
        if (!currentlyHolding.contains(player))
            currentlyHolding.add(player);
    }
    public void removeFromCurrentlyHolding(Player player) { currentlyHolding.remove(player); }
    public ArrayList<Player> getCurrentlyHolding() { return currentlyHolding; }

    // Сделан отдельной переменной, потому что если я внезапно захочу поменять в каком именно слоте будет находиться
    // трекер, то мне придется лезть во все части кода, в которых взаимодействие с этим слотом происходит
    // (например выдача трекера при старте игры или проверка на взятие его в руки). А так я просто поменяю циферку
    // здесь и все будет кайфово
    public static int getReservedSlot() { return trackerReservedInventorySlot; }

    public void setInitialTargets()
    {
        for (Player player : crewManager.getCrewA().getCrewMembers())
            targetMap.put(player, getAppropriateTarget(player));
        for (Player player : crewManager.getCrewB().getCrewMembers())
            targetMap.put(player, getAppropriateTarget(player));
    }

    // метод для выдачи трекера
    public ItemStack getTrackerForPlayer()
    {
        ItemStack tracker = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = tracker.getItemMeta();
        compassMeta.setDisplayName(trackerItemName);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Отслеживает игроков, имеющих флаги, которые вы можете забрать");
        compassMeta.setLore(lore);
        PersistentDataContainer dataContainer = compassMeta.getPersistentDataContainer();
        NamespacedKey soulboundKey = new NamespacedKey(BreadWars.getInstance(), CustomAttribute.SOULBOUND.getKey());
        NamespacedKey fixedSlotKey = new NamespacedKey(BreadWars.getInstance(), CustomAttribute.FIXED_SLOT.getKey());
        // Устанавливаем значение на "привязан к игроку"
        dataContainer.set(soulboundKey, PersistentDataType.INTEGER, 0);
        // Устанавливаем значение на слот на котором должен быть трекер
        dataContainer.set(fixedSlotKey, PersistentDataType.INTEGER, trackerReservedInventorySlot);
        tracker.setItemMeta(compassMeta);
        return tracker;
    }

    public void setCurrentTarget(Player whoNeedsTarget, Player newTarget) { targetMap.put(whoNeedsTarget, newTarget); }
    public Player getCurrentTarget(Player whoNeedsTarget) { return targetMap.get(whoNeedsTarget); }

    // Возвращает первую попавшуюся подходящую цель для игрока. Если таковой нет - возвращается null
    public Player getAppropriateTarget(Player whoNeedsTarget)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(whoNeedsTarget.getUniqueId());
        Crew playerCrew = playerInfo.getCrew();
        Crew oppositeCrew = crewManager.getOppositeCrew(playerCrew);

        // Сначала проверяет на наличие украденных флагов и возвращает обладателя украденного у команды игрока флага
        for (Player player : oppositeCrew.getCrewMembers())
        { if (crewManager.getCrewFlagOwners(playerCrew).contains(player)) return player; }

        // Затем проверяет наличие своих флагов у врагов (ну типа флаг который для врага свой)
        for (Player player : oppositeCrew.getCrewMembers())
        { if (crewManager.getCrewFlagOwners(oppositeCrew).contains(player)) return player; }

        // Если никаких флагов у врагов не осталось, возвращает игрока, которому нужна цель (больше некого возвращать)
        return null;
    }
}
