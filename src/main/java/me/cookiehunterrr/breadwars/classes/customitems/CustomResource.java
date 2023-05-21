package me.cookiehunterrr.breadwars.classes.customitems;

import org.bukkit.Material;

public enum CustomResource
{
    SPIRITUAL_ARROW(new CustomItem("§bДуховная стрела", Material.ARROW, 8, 0));

    CustomItem representingItem;

    CustomResource(CustomItem item)
    {
        representingItem = item;
    }
}
