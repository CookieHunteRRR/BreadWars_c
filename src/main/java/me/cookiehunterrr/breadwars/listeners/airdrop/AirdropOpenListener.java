package me.cookiehunterrr.breadwars.listeners.airdrop;

import me.cookiehunterrr.breadwars.classes.gamesession.GameSession;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class AirdropOpenListener implements Listener
{
    @EventHandler
    public void onAirdropOpen(InventoryOpenEvent e)
    {
        if (e.getInventory().getType() != InventoryType.CHEST) return;
        Location chestLocation = e.getInventory().getLocation();
        Player player = (Player) e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());

        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        GameSession gameSession = playerInfo.getCrew().getCurrentGameSession();
        if (!gameSession.getAirdropManager().doesLocationHasAirdrop(chestLocation)) return;
        gameSession.getAirdropManager().generateAirdropLoot(e.getInventory(), player);
    }
}
