package me.cookiehunterrr.breadwars.classes.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class GUIPage
{
    String pageTitle;
    Inventory pageInventory;

    public GUIPage(int rows, String title)
    {
        pageTitle = title;
        pageInventory = Bukkit.createInventory(null, rows, pageTitle);
    }
}