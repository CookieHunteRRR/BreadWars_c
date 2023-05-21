package me.cookiehunterrr.breadwars.classes.customitems.evo;

import me.cookiehunterrr.breadwars.classes.customitems.CustomAttribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public enum EVOItem
{
    EVO_SWORD(150,5, "§5EVO Sword", new Material[]{
            Material.GOLDEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD}),
    EVO_PICKAXE(80,5, "§5EVO Pickaxe", new Material[]{
            Material.STONE_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.IRON_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE}),
    EVO_AXE(150,5, "§5EVO Axe", new Material[]{
            Material.STONE_AXE,
            Material.GOLDEN_AXE,
            Material.IRON_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE}),
    EVO_BOW(150,3, "§5EVO Bow", new Material[]{
            Material.BOW,
            Material.BOW,
            Material.BOW}),
    EVO_HELMET(200,5, "§5EVO Helmet", new Material[]{
            Material.LEATHER_HELMET,
            Material.GOLDEN_HELMET,
            Material.IRON_HELMET,
            Material.DIAMOND_HELMET,
            Material.NETHERITE_HELMET}),
    EVO_CHESTPLATE(200,5, "§5EVO Chestplate", new Material[]{
            Material.LEATHER_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE,
            Material.IRON_CHESTPLATE,
            Material.DIAMOND_CHESTPLATE,
            Material.NETHERITE_CHESTPLATE}),
    EVO_LEGGINGS(200,5, "§5EVO Leggings", new Material[]{
            Material.LEATHER_LEGGINGS,
            Material.GOLDEN_LEGGINGS,
            Material.IRON_LEGGINGS,
            Material.DIAMOND_LEGGINGS,
            Material.NETHERITE_LEGGINGS}),
    EVO_BOOTS(200,5, "§5EVO Boots", new Material[]{
            Material.LEATHER_BOOTS,
            Material.GOLDEN_BOOTS,
            Material.IRON_BOOTS,
            Material.DIAMOND_BOOTS,
            Material.NETHERITE_BOOTS});

    private static final EVOItem[] values = values();
    final static double expStageMultiplier = 2.25;

    // Опыт, нужный для перехода с первой стадии на вторую. Остальные стадии отсчитываются по формуле в getExpForNextStage
    double baseRequiredExp;
    // Стадии идут от 0 до stageCount - 1
    int stageCount;
    String evoItemName;
    Material[] stageMaterial;

    EVOItem(int baseExp, int stageCount, String itemName, Material[] stageMaterial)
    {
        this.baseRequiredExp = baseExp;
        this.stageCount = stageCount;
        this.evoItemName = itemName;
        this.stageMaterial = stageMaterial;
    }

    public static EVOItem getByOrdinal(int ordinal) { return values[ordinal]; }
    public String getItemTypeAsString()
    { return this.name().toLowerCase().substring(this.name().lastIndexOf("_")+1); }
    public boolean isFinalStage(int stage) { return stage >= (this.stageCount - 1);}
    public int getFinalStage() { return stageCount-1; }
    public Material getStageMaterial(int stage) { return this.stageMaterial[stage]; }

    // baseRequiredExp * (expStageMultiplier ^ currentStage)
    public double getExpForNextStage(int stage)
    {
        return (this.baseRequiredExp * (Math.pow(expStageMultiplier, stage)));
    }

    public ItemStack createEvoItem()
    {
        ItemStack itemToReturn = new ItemStack(this.stageMaterial[0]);
        ItemMeta meta = itemToReturn.getItemMeta();
        meta.setDisplayName(this.evoItemName);
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        // Добавляем привязанность к игроку
        dataContainer.set(CustomAttribute.SOULBOUND.getAsNamespacedKey(), PersistentDataType.INTEGER, 0);
        // Добавляем ЭВО информацию
        dataContainer.set(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType(), new EvoItemInformation(this.ordinal()));
        meta.setUnbreakable(true);
        itemToReturn.setItemMeta(meta);
        return itemToReturn;
    }
}