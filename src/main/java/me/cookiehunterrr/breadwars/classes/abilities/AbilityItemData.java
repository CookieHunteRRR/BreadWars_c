package me.cookiehunterrr.breadwars.classes.abilities;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.abilities.abilities.*;
import me.cookiehunterrr.breadwars.classes.customitems.CustomAttribute;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public enum AbilityItemData
{
    CONQUEROR("Завоеватель", Material.FEATHER, new Conqueror()),
    FREEZING_ARROW("Замораживающая стрела", Material.ICE, new FreezingArrow()),
    AURA_OF_HEALING("Аура лечения", Material.BOOK, new AuraOfHealing()),
    CLOCK_OF_INVISIBILITY("Часы невидимости", Material.CLOCK, new ClockOfInvisibility());

    String abilityName;
    Material abilityMaterial;
    Ability ability;
    final public static int classAbilityReservedInventorySlot = 8;

    AbilityItemData(String name, Material material, Ability ability)
    {
        this.abilityName = name;
        this.abilityMaterial = material;
        this.ability = ability;
    }

    public String getAbilityName() { return abilityName; }
    public Ability getAbility() { return ability; }

    public ItemStack getAbilityAsItemStack()
    {
        ItemStack item = new ItemStack(this.abilityMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.abilityName);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(this.ability.getRequiredJob().jobName);
        meta.setLore(lore);
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey soulboundKey = new NamespacedKey(BreadWars.getInstance(), CustomAttribute.SOULBOUND.getKey());
        NamespacedKey fixedSlotKey = new NamespacedKey(BreadWars.getInstance(), CustomAttribute.FIXED_SLOT.getKey());
        // Устанавливаем значение на "привязан к игроку"
        dataContainer.set(soulboundKey, PersistentDataType.INTEGER, 0);
        // Устанавливаем значение на слот на котором должна быть абилка
        dataContainer.set(fixedSlotKey, PersistentDataType.INTEGER, classAbilityReservedInventorySlot);
        item.setItemMeta(meta);
        return item;
    }
}
