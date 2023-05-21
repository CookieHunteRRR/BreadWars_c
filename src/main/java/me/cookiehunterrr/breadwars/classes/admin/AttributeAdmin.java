package me.cookiehunterrr.breadwars.classes.admin;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.customitems.CustomAttribute;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AttributeAdmin
{
    public static void setItemAttributeToInteger(Player player, String attribute, String value)
    {
        CustomAttribute attributeToSet;
        int valueToSet;
        ItemStack itemToChange;

        try { attributeToSet = CustomAttribute.valueOf(attribute.toUpperCase()); }
        catch (IllegalArgumentException ex)
        { player.sendMessage("§cВведенного атрибута не существует"); return; }
        try { valueToSet = Integer.parseInt(value); }
        catch (NumberFormatException ex) { player.sendMessage("§cЗначение должно быть числом"); return; }

        itemToChange = player.getInventory().getItemInMainHand();
        if (itemToChange.getType() == Material.AIR)
        {
            player.sendMessage("§cВ руке нет предмета");
            return;
        }

        // Проверка на возможность установки такого значения
        switch (attributeToSet)
        {
            case FIXED_SLOT -> {
                if (valueToSet < 0 || valueToSet > 40)
                {
                    player.sendMessage("§cЗначение атрибута FIXED_SLOT должно находится в области значений слотов инвентаря");
                    return;
                }
            }
            case SOULBOUND -> {
                if (valueToSet < 0)
                {
                    player.sendMessage("§cЗначение атрибута SOULBOUND должно быть равно или больше нуля");
                    return;
                }
            }
        }

        NamespacedKey key = attributeToSet.getAsNamespacedKey();
        ItemMeta meta = itemToChange.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(key, PersistentDataType.INTEGER, valueToSet);
        itemToChange.setItemMeta(meta);
        player.sendMessage("§aДля предмета " + itemToChange.getType() + " установлен атрибут " + attributeToSet.name() + " со значением " + valueToSet);
    }
}
