package me.cookiehunterrr.breadwars.commands;

import me.cookiehunterrr.breadwars.classes.chat.ChatChannel;
import me.cookiehunterrr.breadwars.classes.chat.ChatManager;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class ChatCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player player)
        {
            UUID playerID = player.getUniqueId();
            PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(playerID);
            if (playerInfo.getCrew() == null)
            {
                player.sendMessage("§cВы не можете менять канал не находясь в команде");
                return true;
            }

            if (playerInfo.getChatChannel() == ChatChannel.DEFAULT)
                ChatManager.setChatChannelToCrew(player);
            else ChatManager.setChatChannelToDefault(player);
        }
        return true;
    }
}