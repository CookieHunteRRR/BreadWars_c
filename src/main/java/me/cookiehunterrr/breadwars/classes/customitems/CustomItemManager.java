package me.cookiehunterrr.breadwars.classes.customitems;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.customitems.evo.EVOItem;
import me.cookiehunterrr.breadwars.classes.customitems.evo.EvoDataType;
import me.cookiehunterrr.breadwars.classes.customitems.evo.EvoItemInformation;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemManager
{
    Map<CustomItem, ItemStack> cachedCustomItems;

    public CustomItemManager()
    {
        cachedCustomItems = new HashMap<>();
        cacheCustomItems();
    }

    public ItemStack getCustomItem(CustomResource itemToGet)
    {
        return cachedCustomItems.get(itemToGet.representingItem);
    }
    public ItemStack getCustomItem(CustomItem itemToGet)
    {
        return cachedCustomItems.get(itemToGet);
    }

    void cacheCustomItems()
    {
        for (CustomResource resource : CustomResource.values())
        {
            cachedCustomItems.put(resource.representingItem, createItemStack(resource.representingItem));
        }
    }

    public static ItemStack createItemStack(CustomItem itemToCreate)
    {
        ItemStack item = new ItemStack(itemToCreate.material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemToCreate.name);
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey soulboundKey = new NamespacedKey(BreadWars.getInstance(), CustomAttribute.SOULBOUND.getKey());
        NamespacedKey fixedSlotKey = new NamespacedKey(BreadWars.getInstance(), CustomAttribute.FIXED_SLOT.getKey());
        if (itemToCreate.attributesMap.containsKey(CustomAttribute.SOULBOUND))
        {
            dataContainer.set(soulboundKey, PersistentDataType.INTEGER, itemToCreate.attributesMap.get(CustomAttribute.SOULBOUND));
        }
        if (itemToCreate.attributesMap.containsKey(CustomAttribute.FIXED_SLOT))
        {
            dataContainer.set(fixedSlotKey, PersistentDataType.INTEGER, itemToCreate.attributesMap.get(CustomAttribute.FIXED_SLOT));
        }
        item.setItemMeta(meta);
        return item;
    }

    // Обновляет лор, дабы соответствовать нынешним значениям в PersistentDataContainer, если такие есть
    public static void setUpdatedItemMeta(ItemStack item)
    {
        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if (meta.hasLore()) lore = meta.getLore();
        else lore = new ArrayList<>();

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (dataContainer.has(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType()))
        {
            // Создаем лор заново потому что иначе просто поверх друг друга вешаются строки лора короче костыль
            lore = new ArrayList<>();
            EvoItemInformation evoInfo = dataContainer.get(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType());
            EVOItem evoItem = EVOItem.getByOrdinal(evoInfo.getEvoID());
            String exp;
            if (evoItem.isFinalStage(evoInfo.getCurrentStage()))
                exp = "§aExp: Max";
            else
                exp = String.format("§aExp: %.2f / %.2f", evoInfo.getExperience(), evoItem.getExpForNextStage(evoInfo.getCurrentStage()));

            lore.add(exp);
            // specialty позже
        }
        if (dataContainer.has(CustomAttribute.SOULBOUND.getAsNamespacedKey(), PersistentDataType.INTEGER))
        {
            lore.add(""); // Добавляет лишнюю строку для красоты
            int soulbound = dataContainer.get(CustomAttribute.SOULBOUND.getAsNamespacedKey(), PersistentDataType.INTEGER);
            if (soulbound == 0) lore.add("§bПредмет привязан к игроку");
            else lore.add("§bПредмет не падает при смерти");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
