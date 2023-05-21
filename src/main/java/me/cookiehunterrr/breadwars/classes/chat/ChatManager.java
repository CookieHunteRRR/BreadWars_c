package me.cookiehunterrr.breadwars.classes.chat;

import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.entity.Player;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class ChatManager
{
    public static void setChatChannelToDefault(Player player)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        if (playerInfo.getChatChannel() == ChatChannel.CREW)
        {
            playerInfoManager.getPlayerInfo(player.getUniqueId()).setChatChannel(ChatChannel.DEFAULT);
            player.sendMessage("§aТеперь ваши сообщения будут отправляться в общий чат");
        }
    }

    // Проверка на наличие крю проходит вне метода
    public static void setChatChannelToCrew(Player player)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        if (playerInfo.getChatChannel() == ChatChannel.DEFAULT)
        {
            playerInfo.setChatChannel(ChatChannel.CREW);
            player.sendMessage("§aТеперь ваши сообщения будут отправляться в командный чат");
        }
    }
}
