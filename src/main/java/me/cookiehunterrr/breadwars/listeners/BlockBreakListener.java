package me.cookiehunterrr.breadwars.listeners;

import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

// Отвечает за невозможность ломать блоки до начала игры
public class BlockBreakListener implements Listener
{
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        Player player = e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        Crew crew = playerInfo.getCrew();
        if (crew == null) { e.setCancelled(true); return; }
        if (crew.getCurrentGameSession() == null) { e.setCancelled(true); return; }
        if (crew.getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) { e.setCancelled(true); }
    }
}