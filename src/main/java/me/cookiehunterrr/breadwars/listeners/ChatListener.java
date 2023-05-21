package me.cookiehunterrr.breadwars.listeners;

import me.cookiehunterrr.breadwars.classes.chat.ChatChannel;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class ChatListener implements Listener
{
    // Ивент по сути тупо отвечает за то, чтобы во время игры обычный чат отправлялся только команде
    @EventHandler
    public void onPlayerSendMessage(AsyncPlayerChatEvent e)
    {
        // Если ивент триггернулся не из-за игрока, отправившего сообщение
        if (!e.isAsynchronous()) return;
        if (e.getMessage().charAt(0) == '/') return;
        Player player = e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        if (playerInfo.getChatChannel() == ChatChannel.DEFAULT) return;

        e.setCancelled(true);
        playerInfo.getCrew().sendMessageToCrew(player, e.getMessage());
    }
}
