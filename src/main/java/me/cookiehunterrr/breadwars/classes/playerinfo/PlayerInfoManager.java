package me.cookiehunterrr.breadwars.classes.playerinfo;

import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInfoManager
{
    private Map<UUID, PlayerInfo> playerInfoMap;

    public PlayerInfoManager()
    {
        this.playerInfoMap = new HashMap<>();
    }

    public PlayerInfo getPlayerInfo(UUID playerID)
    {
        return playerInfoMap.get(playerID);
    }

    public void registerPlayer(Player player)
    {
        UUID playerID = player.getUniqueId();
        PlayerInfo playerInfo = playerInfoMap.get(playerID);
        if (playerInfo == null) { playerInfoMap.put(playerID, new PlayerInfo()); return; }
        // Если игрок перезашел с активной геймсессией
        try
        {
            if (playerInfo.getCrew().getCurrentGameSession() == null) return;
        }
        catch (Exception ex) { return; }

        // Обновляем объект Player в нужных местах
        playerInfo.getCrew().reconnectPlayer(player);
    }

    public void unregisterPlayer(Player player)
    {
        UUID playerID = player.getUniqueId();
        playerInfoMap.remove(playerID);
    }
}
