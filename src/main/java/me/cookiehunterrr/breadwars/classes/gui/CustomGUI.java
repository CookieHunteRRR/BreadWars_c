package me.cookiehunterrr.breadwars.classes.gui;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CustomGUI
{
    String title;
    int rows;
    boolean isTemplate;
    ArrayList<GUIPage> pages;
    //Player seer;

    public CustomGUI(String title, int rows, boolean isTemplate)
    {
        this.title = title;
        this.rows = rows;
        this.isTemplate = isTemplate;
        this.pages = new ArrayList<>();
    }

    //public void showPage(int pageIndex) { seer.openInventory(pages.get(pageIndex).pageInventory); }
}
