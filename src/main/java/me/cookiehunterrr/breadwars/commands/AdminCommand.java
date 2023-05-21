package me.cookiehunterrr.breadwars.commands;

import me.cookiehunterrr.breadwars.classes.admin.*;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.cookiehunterrr.breadwars.BreadWars.adminUUIDs;
import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class AdminCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player player)
        {
            // Команду не могут использовать не админы
            if (!adminUUIDs.contains(player.getUniqueId())) return false;

            if (args.length < 1) {
                sendHelpMessage(player);
                return true;
            }
            UUID playerID = player.getUniqueId();
            PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(playerID);
            String subcommand = args[0].toLowerCase();

            switch (subcommand)
            {
                case "gs-list" -> GameSessionAdmin.sendGameSessionAdminGUI(player);
                case "gs-getinfo" -> {
                    if (args.length < 2) { player.sendMessage("§cНедостаточно аргументов"); return true; }
                    GameSessionAdmin.getInfoAboutSession(player, args[1]);
                }
                case "mh-info" -> player.sendMessage(player.getInventory().getItemInMainHand().toString());
                case "mh-setatt" -> {
                    if (args.length < 3) { player.sendMessage("§cНедостаточно аргументов"); return true; }
                    AttributeAdmin.setItemAttributeToInteger(player, args[1], args[2]);
                }
                case "pi-get" -> {
                    if (args.length < 2) { player.sendMessage("§cНедостаточно аргументов"); return true; }
                    PlayerInfoAdmin.getPlayerInfo(player, args[1]);
                }
                case "evo-info" -> EvoAdmin.getEvoInfo(player, player.getInventory().getItemInMainHand());
                case "ad-gen" -> {
                    if (args.length < 5) { player.sendMessage("§cНедостаточно аргументов"); return true; }
                    AirdropAdmin.createAirdropOnLocation(player, args[1], args[2], args[3], args[4]);
                }
            }
        }
        return false;
    }

    void sendHelpMessage(Player player)
    {
        player.sendMessage("""
                Возможные команды:
                gs-list, gs-getinfo (index)
                mh-info, mh-setatt (attribute) (value)
                pi-get (nickname)
                evo-info
                ad-gen (tier) (x) (y) (z)""");
    }
}
