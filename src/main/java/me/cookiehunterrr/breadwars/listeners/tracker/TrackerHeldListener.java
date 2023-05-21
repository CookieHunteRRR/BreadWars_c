package me.cookiehunterrr.breadwars.listeners.tracker;

import me.cookiehunterrr.breadwars.classes.gamesession.GameSession;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import me.cookiehunterrr.breadwars.classes.tracker.TrackerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.ArrayList;

import static me.cookiehunterrr.breadwars.BreadWars.crewManager;
import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class TrackerHeldListener implements Listener
{
    @EventHandler
    public void onTrackerEquipOrUnequip(PlayerItemHeldEvent e)
    {
        Player player = e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());

        if (playerInfo.getCrew() == null) return;
        GameSession gameSession = playerInfo.getCrew().getCurrentGameSession();
        if (gameSession == null) return;
        if (gameSession.getSessionState() != SessionState.GAME_ACTIVE) return;
        if (e.getNewSlot() != TrackerManager.getReservedSlot()) return;
        gameSession.addPlayerCurrentlyHoldingTracker(player);
    }

    @EventHandler
    public void onTrackerUse(PlayerInteractEvent e)
    {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        if (playerInfo.getCrew() == null) return;
        GameSession gameSession = playerInfo.getCrew().getCurrentGameSession();
        if (gameSession == null) return;
        if (gameSession.getSessionState() != SessionState.GAME_ACTIVE) return;

        TrackerManager trackerManager = gameSession.getTrackerManager();
        if (!trackerManager.getCurrentlyHolding().contains(player)) return;
        ArrayList<Player> possibleTargets = new ArrayList<>();
        for (Player flagOwner : gameSession.getCrewManager().getCrewFlagOwners(playerInfo.getCrew()))
            if (playerInfoManager.getPlayerInfo(flagOwner.getUniqueId()).getCrew() != playerInfo.getCrew())
                possibleTargets.add(flagOwner);
        if (possibleTargets.size() < 2) return;

        Player newTarget;
        int currentTargetIndex = possibleTargets.indexOf(trackerManager.getCurrentTarget(player));

        if (currentTargetIndex + 1 < possibleTargets.size()) newTarget = possibleTargets.get(currentTargetIndex + 1);
        else newTarget = possibleTargets.get(0);

        player.sendMessage("[§2Трекер§r] Новая цель: " + newTarget.getDisplayName());
        trackerManager.setCurrentTarget(player, newTarget);
    }
}
