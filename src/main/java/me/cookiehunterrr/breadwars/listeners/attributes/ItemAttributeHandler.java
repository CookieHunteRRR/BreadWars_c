package me.cookiehunterrr.breadwars.listeners.attributes;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.customitems.CustomAttribute;
import me.cookiehunterrr.breadwars.listeners.BlockPlaceListener;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemAttributeHandler implements Listener
{
    // Отменяет клик в инвентаре если предмет находится на фиксированном слоте
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if (e.getCurrentItem() == null) return;
        ItemMeta meta = e.getCurrentItem().getItemMeta();
        if (meta == null) return;
        PersistentDataContainer persistentData = meta.getPersistentDataContainer();
        NamespacedKey fixedSlotKey = new NamespacedKey(BreadWars.getInstance(), CustomAttribute.FIXED_SLOT.getKey());

        if (persistentData.has(fixedSlotKey, PersistentDataType.INTEGER))
        {
            int slot = persistentData.get(fixedSlotKey, PersistentDataType.INTEGER);
            // Если у предмета есть аттрибут fixedslot и предмет находится в слоте в котором он и должен быть
            // отменяем ивент
            if (e.getSlot() == slot) e.setCancelled(true);
            else
            {
                System.out.println(ChatColor.RED + "[BreadWars Attributes] Player " + e.getWhoClicked().getName() +
                        " has item with fixed slot not in the fixed slot (item in slot " + e.getSlot() + ", must be " +
                        slot + ")");
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e)
    {
        ItemMeta meta = e.getItemDrop().getItemStack().getItemMeta();
        if (meta == null) return;
        PersistentDataContainer persistentData = meta.getPersistentDataContainer();
        NamespacedKey soulboundKey = new NamespacedKey(BreadWars.getInstance(), CustomAttribute.SOULBOUND.getKey());

        if (persistentData.has(soulboundKey, PersistentDataType.INTEGER))
        {
            int soulboundType = persistentData.get(soulboundKey, PersistentDataType.INTEGER);
            // Если у предмета есть аттрибут soulbound и он равен 0 (привязан к игроку)
            // отменяем ивент
            if (soulboundType == 0) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e)
    {
        ItemMeta meta = e.getItemInHand().getItemMeta();
        if (meta == null) return;
        PersistentDataContainer persistentData = meta.getPersistentDataContainer();
        NamespacedKey soulboundKey = new NamespacedKey(BreadWars.getInstance(), CustomAttribute.SOULBOUND.getKey());

        if (persistentData.has(soulboundKey, PersistentDataType.INTEGER))
        {
            int soulboundType = persistentData.get(soulboundKey, PersistentDataType.INTEGER);
            // Если у предмета есть аттрибут soulbound и он равен 0 (привязан к игроку)
            // отменяем ивент
            if (soulboundType == 0) e.setCancelled(true);
        }
    }
}
