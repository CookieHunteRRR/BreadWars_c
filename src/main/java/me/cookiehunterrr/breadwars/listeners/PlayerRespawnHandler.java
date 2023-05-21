package me.cookiehunterrr.breadwars.listeners;

import me.cookiehunterrr.breadwars.classes.Constants;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.customitems.CustomAttribute;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class PlayerRespawnHandler implements Listener
{
    final static int minDistanceForRespawn = Constants.minDistanceBetweenBases / 2;

    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent e)
    {
        e.setUseBed(Event.Result.ALLOW);
        e.getPlayer().setBedSpawnLocation(e.getBed().getLocation(), true);
        //e.getPlayer().sendMessage("§3Теперь вы будете возрождаться в этом месте, даже если вашу кровать сломают");
    }

    @EventHandler
    public void handlePlayerRespawnLocation(PlayerRespawnEvent e)
    {
        Player player = e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        // Если кровать игрока не сломана, останавливаем метод потому что его реснет на кровати
        if (player.getBedSpawnLocation() != null) return;
        SessionCrewManager crewManager = playerInfo.getCrew().getCurrentGameSession().getCrewManager();
        // В противном случае, респавним на дефолтной точке респавна команды
        e.setRespawnLocation(crewManager.getCrewGameInfo(playerInfo.getCrew()).getRespawnLocation());
    }

    @EventHandler
    public void handlePlayerInventoryOnRespawn(PlayerRespawnEvent e)
    {
        Player player = e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        ArrayList<ItemStack> itemsToReturn = playerInfo.retrieveAndClearSavedItems();
        // Сначала нужно расставить по нужным местам предметы, имеющие атрибут fixed_slot
        // Находим предметы, имеющие фиксед слот и заносим их в отдельный лист
        ArrayList<ItemStack> itemsWithFixedSlot = new ArrayList<>();
        NamespacedKey fixedSlotKey = CustomAttribute.FIXED_SLOT.getAsNamespacedKey();
        // Заносим предметы с fixedSlot в отдельный лист, попутно расставляя их по местам
        for (ItemStack item : itemsToReturn)
        {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer itemData = meta.getPersistentDataContainer();
            if (!itemData.has(fixedSlotKey, PersistentDataType.INTEGER)) continue;
            Integer fixedSlot = itemData.get(fixedSlotKey, PersistentDataType.INTEGER);
            // Проверка строкой ниже вообще не нужна, но меня бесит что в ИДЕ оно подсвечивает как теоретический NullPointerException
            if (fixedSlot == null) continue;
            // Проверяем является ли слот - слотом для брони
            if (fixedSlot >= 36 && fixedSlot <= 39)
            {
                int temp = fixedSlot;
                itemData.remove(fixedSlotKey);
                item.setItemMeta(meta);
                switch (temp)
                {
                    case 39 -> { player.getInventory().setHelmet(item); }
                    case 38 -> { player.getInventory().setChestplate(item); }
                    case 37 -> { player.getInventory().setLeggings(item); }
                    case 36 -> { player.getInventory().setBoots(item); }
                }
                itemsWithFixedSlot.add(item);
                continue;
            }
            player.getInventory().setItem(fixedSlot, item);
            itemsWithFixedSlot.add(item);
        }
        // Убираем из itemsToReturn предметы в itemsWithFixedSlot
        // Я бы с радостью сделал всю эту махинацию одним циклом, если бы я мог удалять элементы списка, пока происходит его итерация
        for (ItemStack itemToRemove : itemsWithFixedSlot)
            itemsToReturn.remove(itemToRemove);
        // Теперь у нас должен был остаться список предметов, который можно выдать в любом порядке
        for (ItemStack item : itemsToReturn)
            player.getInventory().addItem(item);
    }
}
