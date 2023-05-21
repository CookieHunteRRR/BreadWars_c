package me.cookiehunterrr.breadwars.classes.admin;

import me.cookiehunterrr.breadwars.classes.gamesession.GameSession;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class AirdropAdmin
{
    public static void createAirdropOnLocation(Player player, String tier, String x, String y, String z)
    {
        World world = player.getWorld();
        int xi, yi, zi;
        int tieri;
        try { xi = Integer.parseInt(x); }
        catch (NumberFormatException ex) { player.sendMessage("§cЗначение x должно быть числом"); return; }
        try { yi = Integer.parseInt(y); }
        catch (NumberFormatException ex) { player.sendMessage("§cЗначение y должно быть числом"); return; }
        try { zi = Integer.parseInt(z); }
        catch (NumberFormatException ex) { player.sendMessage("§cЗначение z должно быть числом"); return; }
        try { tieri = Integer.parseInt(tier); }
        catch (NumberFormatException ex) { player.sendMessage("§cЗначение tier должно быть числом"); return; }
        if (tieri < 1) { player.sendMessage("§cЗначение tier должно быть больше 0"); return; }
        Block blockToCreateAirdrop = world.getBlockAt(xi, yi, zi);
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());

        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE)
            {
                player.sendMessage("§cСоздать аирдроп можно только находясь в рабочей геймсессии"); return;
            }
        }
        catch (Exception ex) { player.sendMessage("§cСоздать аирдроп можно только находясь в рабочей геймсессии"); return; }

        GameSession gameSession = playerInfo.getCrew().getCurrentGameSession();
        Location blockLoc = blockToCreateAirdrop.getLocation();
        gameSession.getAirdropManager().createAirdropOnLocation(blockLoc, tieri);
        String location = "(" + blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ")";
        player.sendMessage(String.format("§aНа координатах %s сгенерирован аирдроп с тиром %d", location, tieri));
    }
}
