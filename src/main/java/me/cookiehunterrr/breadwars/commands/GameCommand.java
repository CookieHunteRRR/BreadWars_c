package me.cookiehunterrr.breadwars.commands;

import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.gamesession.GameSession;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import me.cookiehunterrr.breadwars.tasks.SessionTaskInfo;
import me.cookiehunterrr.breadwars.tasks.gamesession.CrewReadyToPlayTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

import static me.cookiehunterrr.breadwars.BreadWars.*;

public class GameCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player player)
        {
            UUID playerID = player.getUniqueId();
            PlayerInfo senderPlayerInfo = playerInfoManager.getPlayerInfo(playerID);
            if (senderPlayerInfo.getCrew() == null)
            {
                player.sendMessage("§cВы должны состоять в команде, чтобы использовать эту команду");
                return true;
            }
            if (args.length < 1)
            {
                sendHelpMessage(player);
                return true;
            }

            String subcommand = args[0].toLowerCase();
            // Ограничение возможных команд во время игровой сессии
            if (senderPlayerInfo.getCrew().getCurrentGameSession() != null)
            {
                if (senderPlayerInfo.getCrew().getCurrentGameSession().getSessionState() == SessionState.GAME_ACTIVE)
                {
                    // Перечисление доступных команд
                    if (!subcommand.equals("help"))
                    {
                        player.sendMessage("§cВы не можете использовать эту команду во время активной игровой сессии");
                        return true;
                    }
                }
            }

            switch (subcommand)
            {
                case "help" -> sendHelpMessage(player);

                // Вызов на бой
                case "challenge" -> {
                    // Стадия проверок
                    Crew senderCrew = senderPlayerInfo.getCrew();
                    if (senderCrew.getCrewLeader() != player)
                    {
                        player.sendMessage("§cТолько лидер способен вызвать другую команду на бой");
                        return true;
                    }
                    if (args.length != 2)
                    {
                        player.sendMessage("§cВы не указали название команды, либо указали избыточное количество аргументов");
                        return true;
                    }
                    if (Objects.equals(args[1], senderCrew.getCrewName()))
                    {
                        player.sendMessage("§cВы не можете вызвать на бой свою же команду");
                        return true;
                    }
                    Crew enemyCrew = crewManager.getCrewByName(args[1]);
                    if (enemyCrew == null)
                    {
                        player.sendMessage("§cВы указали несуществующее название команды");
                        return true;
                    }
                    if (senderCrew.getCurrentGameSession() != null || enemyCrew.getCurrentGameSession() != null)
                    {
                        player.sendMessage("§cВаша команда или команда противника уже находятся в игровой сессии");
                        return true;
                    }

                    // Создание геймсешшена в состоянии ожидания ответа
                    gameSessionManager.addSession(new GameSession(senderCrew, enemyCrew));
                    sendChallengeMessage(enemyCrew.getCrewLeader(), senderCrew.getCrewName());
                    player.sendMessage("§3Вы вызвали команду " + enemyCrew.getCrewName() + " на бой");
                }

                case "accept" -> {
                    // Стадия проверок
                    Crew senderCrew = senderPlayerInfo.getCrew();
                    if (senderCrew.getCrewLeader() != player)
                    {
                        player.sendMessage("§cТолько лидер способен принять вызов на бой");
                        return true;
                    }
                    if (senderCrew.getCurrentGameSession() == null)
                    {
                        player.sendMessage("§cВаша команда не получала никаких вызовов на бой");
                        return true;
                    }
                    if (senderCrew.getCurrentGameSession().getSessionState() != SessionState.SESSION_AWAITING_RESPONSE)
                    {
                        player.sendMessage("§cВы не можете принять уже запущенную сессию");
                        return true;
                    }

                    // Смена стадии сессии на сформированную (в ожидании согласия на запуск игры)
                    senderCrew.getCurrentGameSession().changeStateToFormed();
                }

                case "start" -> {
                    // Стадия проверок
                    Crew senderCrew = senderPlayerInfo.getCrew();
                    if (senderCrew.getCrewLeader() != player)
                    {
                        player.sendMessage("§cТолько лидер способен согласиться на запуск игры");
                        return true;
                    }
                    if (senderCrew.getCurrentGameSession() == null)
                    {
                        player.sendMessage("§cВаша команда не находится в игровой сессии");
                        return true;
                    }
                    if (senderCrew.getCurrentGameSession().getSessionState() != SessionState.SESSION_FORMED)
                    {
                        player.sendMessage("§cВы не можете согласиться на запуск игры, так как игра либо уже " +
                                "запущена, либо противник не принял вызов");
                        return true;
                    }
                    if (!senderCrew.doEveryPlayerHaveJob())
                    {
                        player.sendMessage("§cВы не можете начать игру, если в вашей команде есть игроки, не " +
                                "выбравшие класс");
                        return true;
                    }

                    // Согласие на запуск игры
                    if (senderCrew.getCurrentGameSession().getTaskManager().getActiveTasksCount() != 0)
                        senderCrew.getCurrentGameSession().changeStateToStarted();
                    else
                    {
                        SessionCrewManager crewManager = senderCrew.getCurrentGameSession().getCrewManager();
                        SessionTaskInfo taskInfo = new CrewReadyToPlayTask(senderCrew, crewManager).runTaskAndGetTaskInfo();
                        senderCrew.getCurrentGameSession().getTaskManager().registerTask(taskInfo);
                    }
                }
            }
        }
        return false;
    }

    void sendHelpMessage(Player player)
    {
        player.sendMessage("""
                §3=== Команда game ===
                §a/game help§r - вызов данного сообщения
                §a/game challenge <название>§r - вызывает на бой указанную команду
                §a/game accept§r - принимает вызов, если вашей команде его бросили
                §a/game start§r - согласие на начало боя, если вызов принят""");
    }

    void sendChallengeMessage(Player player, String challengerCrewName)
    {
        player.sendMessage("§3================\n" +
                "§aКоманда " + challengerCrewName + " предлагает сразиться с вашей командой\n" +
                "Напишите §3/game accept§a, чтобы принять вызов\n" +
                "§3================");
    }
}
