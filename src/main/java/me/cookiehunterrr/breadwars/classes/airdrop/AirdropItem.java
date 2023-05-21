package me.cookiehunterrr.breadwars.classes.airdrop;

import org.bukkit.inventory.ItemStack;

public class AirdropItem
{
    final ItemStack item;
    final int weight;
    final int minAirdropTier;
    final int maxAirdropTier;

    public AirdropItem(ItemStack item, int weight, int minTier)
    {
        this.item = item;
        this.weight = weight;
        this.minAirdropTier = minTier;
        this.maxAirdropTier = -1;
    }

    public AirdropItem(ItemStack item, int weight, int minTier, int maxTier)
    {
        this.item = item;
        this.weight = weight;
        this.minAirdropTier = minTier;
        this.maxAirdropTier = maxTier;
    }

    public ItemStack getItem() { return item; }
    public int getWeight() { return weight; }
    public int getMinAirdropTier() { return minAirdropTier; }
    public int getMaxAirdropTier() { return maxAirdropTier; }
}
