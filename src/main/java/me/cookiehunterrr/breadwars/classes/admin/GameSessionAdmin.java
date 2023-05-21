package me.cookiehunterrr.breadwars.classes.admin;

import me.cookiehunterrr.breadwars.classes.Utils;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.gamesession.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cookiehunterrr.breadwars.BreadWars.gameSessionManager;

public class GameSessionAdmin
{
    public static void sendGameSessionAdminGUI(Player player)
    {
        Inventory gameSessionAdmin = Bukkit.createInventory(null, 27, "GameSession Admin");
        ArrayList<GameSession> allSessions = gameSessionManager.getAllSessions();
        for (GameSession gs : allSessions)
            gameSessionAdmin.addItem(getGameSessionAsItem(gs, allSessions.indexOf(gs)));
        player.openInventory(gameSessionAdmin);
    }

    static ItemStack getGameSessionAsItem(GameSession gs, int gsIndex)
    {
        ItemStack sessionItem = new ItemStack(Material.BEDROCK);
        ItemMeta meta = sessionItem.getItemMeta();
        meta.setDisplayName("GameSession #" + gsIndex);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§aSessionState: " + gs.getSessionState().name());
        lore.add("");
        Crew crewA = gs.getCrewManager().getCrewA();
        lore.add("§aCrewA (" + crewA.getCrewName() + ")");
        for (Player player : crewA.getCrewMembers())
        {
            lore.add(" | " + player.getDisplayName());
        }
        lore.add("§aCrewB (" + gs.getCrewManager().getCrewB().getCrewName() + ")");
        Crew crewB = gs.getCrewManager().getCrewB();
        for (Player player : crewB.getCrewMembers())
        {
            lore.add(" | " + player.getDisplayName());
        }

        lore.add("§aActive tasks: " + gs.getTaskManager().getActiveTasksCount());
        if (gs.getWhenGameStarted() != 0)
            lore.add("§aTime passed since game started: " + Utils.getTimeSinceValueAsString(gs.getWhenGameStarted()));

        meta.setLore(lore);
        sessionItem.setItemMeta(meta);
        return sessionItem;
    }

    public static void getInfoAboutSession(Player player, String index)
    {
        int gsIndex;
        try { gsIndex = Integer.parseInt(index); }
        catch (NumberFormatException ex) { player.sendMessage("§cИндекс должен быть числом"); return; }

        GameSession gameSession;
        try { gameSession = gameSessionManager.getSessionByIndex(gsIndex); }
        catch (IndexOutOfBoundsException ex) { player.sendMessage("§cТакого индекса не существует"); return; }

        SessionCrewManager crewManager = gameSession.getCrewManager();
        //TrackerManager trackerManager = gameSession.getTrackerManager();

        StringBuilder sb = new StringBuilder();
        String header = "GameSession #" + index + " (" + crewManager.getCrewA().getCrewName() + " vs " + crewManager.getCrewB().getCrewName() + ")\n";
        sb.append(header);
        sb.append("Current State: ").append(gameSession.getSessionState()).append("\n");
        String whenStarted = "Started: " + gameSession.getWhenGameStarted() + " (" +
                Utils.getTimeSinceValueAsString(gameSession.getWhenGameStarted()) + " ago)\n\n";
        sb.append(whenStarted);

        sb.append("CrewA:\n").append("Flag owners: ");
        for (Player flagOwner : crewManager.getCrewFlagOwners(crewManager.getCrewA()))
        {
            sb.append(flagOwner.getDisplayName()).append(" ");
        }
        sb.append("\n");
        sb.append("Crew score: ").append(crewManager.getCrewScore(crewManager.getCrewA())).append("\n");
        sb.append("\n");

        sb.append("CrewB:\n").append("Flag owners: ");
        for (Player flagOwner : crewManager.getCrewFlagOwners(crewManager.getCrewB()))
        {
            sb.append(flagOwner.getDisplayName()).append(" ");
        }
        sb.append("\n");
        sb.append("Crew score: ").append(crewManager.getCrewScore(crewManager.getCrewB())).append("\n");

        player.sendMessage(sb.toString());
    }
}
