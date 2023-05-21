package me.cookiehunterrr.breadwars.listeners;

import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.cookiehunterrr.breadwars.BreadWars.crewManager;
import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class JoinAndQuitHandler implements Listener
{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        // Метод сам проверяет, существует ли плейеринфо для этого игрока
        playerInfoManager.registerPlayer(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        Player player = e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        if (playerInfo == null) return;

        Crew crew = playerInfo.getCrew();
        if (crew != null)
        {
            if (crew.getCurrentGameSession() != null) return;

            crew.removePlayer(player);
            if (crew.getCrewMembers().size() < 1)
            {
                String crewName = crew.getCrewName();
                crewManager.deleteCrew(crew);
                System.out.printf("[BreadWars Crews] Crew %s removed, because all players left\n", crewName);
            }
            else if (player == crew.getCrewLeader())
            {
                Player nextLeader = crew.getCrewMembers().get(0);
                crew.setCrewLeader(nextLeader);
                nextLeader.sendMessage("§3Вы стали лидером команды " + crew.getCrewName() + ", так как предыдущий лидер покинул игру");
                System.out.printf("[BreadWars Crews] %s became the leader of %s, because previous leader left\n",
                        nextLeader.getDisplayName(), crew.getCrewName());
            }
        }
        playerInfoManager.unregisterPlayer(player);
        System.out.printf("[BreadWars] Removed %s's playerInfo, because they left\n", player.getDisplayName());
    }
}
