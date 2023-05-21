package me.cookiehunterrr.breadwars.listeners.admin;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

public class AdminGUIListener implements Listener
{
    static ArrayList<String> inventoryTitles;

    public AdminGUIListener()
    {
        inventoryTitles = new ArrayList<>();

        inventoryTitles.add("GameSession Admin");
        inventoryTitles.add("GameSession Admin > Edit");
    }

    @EventHandler
    public void onGUIInteraction(InventoryClickEvent e)
    {
        if (!inventoryTitles.contains(e.getView().getTitle())) return;

        e.setCancelled(true);
        switch (e.getView().getTitle())
        {
            case "GameSession Admin" -> {
                // Если нажимаем по геймсессии
                if (e.getCurrentItem().getType() == Material.BEDROCK)
                {

                }
            }
            case "GameSession Admin > Edit" -> {

            }
            default -> {

            }
        }
    }
}
