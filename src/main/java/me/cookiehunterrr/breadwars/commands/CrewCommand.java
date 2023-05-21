package me.cookiehunterrr.breadwars.commands;

import me.cookiehunterrr.breadwars.classes.Constants;
import me.cookiehunterrr.breadwars.classes.Utils;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static me.cookiehunterrr.breadwars.BreadWars.crewManager;
import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class CrewCommand implements CommandExecutor
{
    final ArrayList<String> availableCommands;

    public CrewCommand()
    {
        // Перечисление доступных команд
        availableCommands = new ArrayList<>();
        availableCommands.add("help");
        availableCommands.add("flags");
        availableCommands.add("chat");
        availableCommands.add("setbase");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player player)
        {
            if (args.length < 1)
            {
                sendHelpMessage(player);
                return true;
            }
            UUID playerID = player.getUniqueId();
            PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(playerID);
            Crew playerCrew = playerInfo.getCrew();
            String subcommand = args[0].toLowerCase();

            if (playerCrew != null)
            {
                if (playerCrew.getCurrentGameSession() != null)
                {
                    if (playerCrew.getCurrentGameSession().getSessionState() == SessionState.GAME_ACTIVE)
                    {
                        if (!availableCommands.contains(subcommand))
                        {
                            player.sendMessage("§cВы не можете использовать эту команду во время активной игровой сессии");
                            return true;
                        }
                    }
                }
            }

            switch (subcommand) {
                case "help" -> sendHelpMessage(player);

                case "chat" -> {
                    if (playerCrew == null)
                    {
                        player.sendMessage("§cВы не можете использовать эту команду, не находясь в команде");
                        return true;
                    }
                    StringBuilder sb = new StringBuilder();
                    String[] toSend = Arrays.copyOfRange(args, 1, args.length);
                    for (String s : toSend)
                        sb.append(s).append(" ");
                    for (Player crewmate : playerCrew.getCrewMembers())
                        crewmate.sendMessage("§a[Crew] " + player.getDisplayName() + ":§r " + sb);
                }

                case "flags" -> {
                    if (playerCrew == null)
                    {
                        player.sendMessage("§cВы не можете использовать эту команду вне игровой сессии");
                        return true;
                    }
                    if (playerCrew.getCurrentGameSession() == null)
                    {
                        player.sendMessage("§cВы не можете использовать эту команду вне игровой сессии");
                        return true;
                    }
                    if (playerCrew.getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE)
                    {
                        player.sendMessage("§cВы можете использовать эту команду только в начатой игровой сессии");
                        return true;
                    }

                    StringBuilder sb = new StringBuilder();
                    for (Player crewmate : playerCrew.getCrewMembers())
                    {
                        String crewmateFlags = playerCrew.getCurrentGameSession().getCrewManager().getPlayerFlagInventoryAsString(crewmate);
                        sb.append("§r").append(crewmate.getDisplayName()).append(": ").append(crewmateFlags).append("\n");
                    }
                    player.sendMessage("§3Инвентари флагов ваших союзников:§r\n" + sb);
                }

                case "setbase" -> {
                    if (playerCrew == null)
                    {
                        player.sendMessage("§cВы не можете использовать эту команду вне игровой сессии");
                        return true;
                    }
                    if (playerCrew.getCurrentGameSession() == null)
                    {
                        player.sendMessage("§cВы не можете использовать эту команду вне игровой сессии");
                        return true;
                    }
                    if (playerCrew.getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE)
                    {
                        player.sendMessage("§cВы можете использовать эту команду только в начатой игровой сессии");
                        return true;
                    }
                    if (playerCrew.getCrewLeader() != player)
                    {
                        player.sendMessage("§cТолько лидеры могут использовать эту команду");
                        return true;
                    }
                    Location newLocation = player.getLocation();
                    SessionCrewManager sessionCrewManager = playerCrew.getCurrentGameSession().getCrewManager();
                    Crew enemyCrew = sessionCrewManager.getOppositeCrew(playerCrew);
                    Location enemyLocation = sessionCrewManager.getCrewGameInfo(enemyCrew).getRespawnLocation();
                    if (Utils.getDistanceBetweenLocations(newLocation, enemyLocation) < Constants.minDistanceBetweenBases)
                    {
                        player.sendMessage("§cВы не можете установить точку возрождения команды " +
                                "меньше чем за " + Constants.minDistanceBetweenBases +
                                " блоков от вражеской базы");
                        return true;
                    }
                    sessionCrewManager.getCrewGameInfo(playerCrew).setRespawnLocation(newLocation);
                    player.sendMessage("§aВы успешно установили новую точку возрождения вашей команды");
                }

                case "create" -> {
                    if (args.length > 2)
                        player.sendMessage("§cНазвание команды состоит из 1 слова, остальные проигнорированы");
                    if (args.length >= 2)
                        crewManager.registerCrew(player, args[1]);
                    else
                        crewManager.registerCrew(player, "NewCrew#" + crewManager.getCrewCount());
                }

                case "delete" -> {
                    if (playerCrew == null)
                    {
                        player.sendMessage("§cВы не состоите в команде");
                        return true;
                    }
                    if (playerCrew.getCrewLeader() != player)
                    {
                        player.sendMessage("§cТолько лидеры могут использовать эту команду");
                        return true;
                    }
                    String crewName = playerCrew.getCrewName();
                    crewManager.deleteCrew(playerCrew);
                    player.sendMessage("§aВы успешно удалили свою команду " + crewName);
                }

                case "join" -> {
                    if (args.length != 2)
                    {
                        player.sendMessage("§cВы не указали название команды, либо указали избыточное количество аргументов");
                        return true;
                    }
                    crewManager.joinCrew(player, args[1]);
                }

                case "leave" -> crewManager.leaveCrew(player);

                //case "list" -> player.openInventory(crewManager.outputCrewsToInventory());

                default -> {
                    player.sendMessage("§cНеизвестный аргумент");
                    sendHelpMessage(player);
                }
            }
        }
        return true;
    }

    void sendHelpMessage(Player player)
    {
        player.sendMessage("""
                §3=== Команда crew ===
                §a/crew help§r - вызов данного сообщения
                §a/crew create <название>§r - создает команду с указанным названием
                §a/crew delete§r - удаляет вашу команду, если вы лидер команды
                §a/crew join <название>§r - добавляет вас в указанную команду
                §a/crew leave§r - удаляет вас из команды
                §a/crew chat <сообщение>§r - отправляет сообщение, которое увидят только ваши союзники
                §a/crew flags§r - (ТОЛЬКО ВО ВРЕМЯ ИГРОВОЙ СЕССИИ) показывает состояние флагов вашей команды в чате""");
    }
}
