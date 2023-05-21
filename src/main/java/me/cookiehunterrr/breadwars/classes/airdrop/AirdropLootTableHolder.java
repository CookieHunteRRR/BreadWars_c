package me.cookiehunterrr.breadwars.classes.airdrop;

import me.cookiehunterrr.breadwars.classes.customitems.CustomItem;
import me.cookiehunterrr.breadwars.classes.customitems.CustomItemManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class AirdropLootTableHolder
{
    final public static ArrayList<AirdropItem> airdropLootTable = generateLootTable();

    private static ArrayList<AirdropItem> generateLootTable()
    {
        ArrayList<AirdropItem> table = new ArrayList<>();

        // tier 1
        table.add(new AirdropItem(new ItemStack(Material.PUMPKIN_PIE, 16), 8000, 1, 6));
        table.add(new AirdropItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 16), 8000, 1, 4));
        table.add(new AirdropItem(new ItemStack(Material.ENDER_PEARL, 4), 7000, 1, 6));
        table.add(new AirdropItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 32), 6000, 1));
        table.add(new AirdropItem(new ItemStack(Material.BOOKSHELF, 16), 6000, 1, 3));
        table.add(new AirdropItem(new ItemStack(Material.PUMPKIN_PIE, 32), 5000, 1));
        table.add(new AirdropItem(new ItemStack(Material.BLAZE_POWDER, 16), 5000, 1, 5));
        table.add(new AirdropItem(new ItemStack(Material.CAKE), 4000, 1, 3));
        table.add(new AirdropItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 64), 4000, 1));
        table.add(new AirdropItem(new ItemStack(Material.BREWING_STAND), 4000, 1, 3));
        table.add(new AirdropItem(new ItemStack(Material.ENCHANTING_TABLE), 4000, 1, 3));
        table.add(new AirdropItem(new ItemStack(Material.BOOKSHELF, 32), 4000, 1, 3));
        table.add(new AirdropItem(new ItemStack(Material.FIREWORK_ROCKET, 16), 3000, 1, 4));
        table.add(new AirdropItem(new ItemStack(Material.IRON_HORSE_ARMOR), 3000, 1, 4));
        table.add(new AirdropItem(new ItemStack(Material.BLAZE_POWDER, 32), 2500, 1, 6));
        table.add(new AirdropItem(new ItemStack(Material.FIREWORK_ROCKET, 32), 1500, 1, 6));
        table.add(new AirdropItem(new ItemStack(Material.GOLDEN_APPLE, 4), 1000, 1));
        // tier 2
        table.add(new AirdropItem(new ItemStack(Material.ENDER_PEARL, 8), 5000, 2, 7));
        table.add(new AirdropItem(new ItemStack(Material.ENDER_CHEST), 3000, 2));
        table.add(new AirdropItem(new ItemStack(Material.FIREWORK_ROCKET, 64), 1000, 2));
        table.add(new AirdropItem(createOPPotion(PotionEffectType.SPEED, 20, 2), 1000, 2));
        table.add(new AirdropItem(new ItemStack(Material.GOLDEN_APPLE, 8), 500, 2));
        // tier 3
        table.add(new AirdropItem(new ItemStack(Material.ENDER_PEARL, 16), 3000, 3));
        table.add(new AirdropItem(new ItemStack(Material.DIAMOND_HORSE_ARMOR), 1500, 3, 8));
        // tier 4
        table.add(new AirdropItem(new ItemStack(Material.GOLDEN_APPLE, 16), 250, 4));
        table.add(new AirdropItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE), 500, 4));
        table.add(new AirdropItem(createTippedArrowOfHarming(4), 500, 4));
        table.add(new AirdropItem(createFunnyElytra(), 750, 4));
        // tier 5
        table.add(new AirdropItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 2), 250, 5));
        table.add(new AirdropItem(createOPPotion(PotionEffectType.INCREASE_DAMAGE, 20, 2), 500, 5));
        // tier 6
        table.add(new AirdropItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 4), 100, 6));
        table.add(new AirdropItem(createTippedArrowOfHarming(8), 250, 6));
        table.add(new AirdropItem(new ItemStack(Material.TOTEM_OF_UNDYING), 100, 6));

        return table;
    }

    static ItemStack createTippedArrowOfHarming(int quantity)
    {
        ItemStack item = new ItemStack(Material.TIPPED_ARROW, quantity);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
        item.setItemMeta(meta);
        return item;
    }

    static ItemStack createOPPotion(PotionEffectType potionEffectType, int seconds, int amp)
    {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(potionEffectType, 20*seconds, amp), true);
        potion.setItemMeta(meta);
        return potion;
    }

    static ItemStack createFunnyElytra()
    {
        CustomItem customElytra = new CustomItem("§5Смешная элитра", Material.ELYTRA, null, 1);
        ItemStack elytra = CustomItemManager.createItemStack(customElytra);
        CustomItemManager.setUpdatedItemMeta(elytra);
        ItemMeta meta = elytra.getItemMeta();
        meta.setUnbreakable(true);
        elytra.setItemMeta(meta);
        return elytra;
    }
}
