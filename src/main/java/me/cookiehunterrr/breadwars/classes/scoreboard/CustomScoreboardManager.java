package me.cookiehunterrr.breadwars.classes.scoreboard;

import me.cookiehunterrr.breadwars.classes.Utils;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class CustomScoreboardManager
{
    ScoreboardManager bukkitScoreboardManager;
    Scoreboard scoreboard;
    SessionCrewManager sessionCrewManager;
    long sessionStartTime;

    public CustomScoreboardManager(SessionCrewManager sessionCrewManager, long time)
    {
        this.bukkitScoreboardManager = Bukkit.getScoreboardManager();
        this.sessionCrewManager = sessionCrewManager;
        this.sessionStartTime = time;
    }

    public void updateScoreboardsForPlayers()
    {
        Scoreboard sb = getUpdatedScoreboard();
        for (Player player : sessionCrewManager.getAllPlayersInSession())
            player.setScoreboard(sb);
    }

    public Scoreboard getUpdatedScoreboard()
    {
        Crew crewA = sessionCrewManager.getCrewA();
        Crew crewB = sessionCrewManager.getCrewB();

        scoreboard.getTeam("crewAFlags").setPrefix("Флаги: " + sessionCrewManager.getCrewFlagOwnersAsString(crewA));
        scoreboard.getTeam("crewAScore").setPrefix("Очки: " + sessionCrewManager.getCrewScore(crewA));
        scoreboard.getTeam("crewBFlags").setPrefix("Флаги: " + sessionCrewManager.getCrewFlagOwnersAsString(crewB));
        scoreboard.getTeam("crewBScore").setPrefix("Очки: " + sessionCrewManager.getCrewScore(crewB));
        scoreboard.getTeam("timeCounter").setPrefix("Время: " + Utils.getTimeSinceValueAsString(sessionStartTime));

        return scoreboard;
    }

    public void createInitialScoreboard()
    {
        this.scoreboard = bukkitScoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("sessionSideBoard", "dummy", "§aBreadWars");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Crew crewA = sessionCrewManager.getCrewA();
        Crew crewB = sessionCrewManager.getCrewB();

        objective.getScore("Команда " + crewA.getCrewName()).setScore(10);

        Team crewAFlagsTeam = scoreboard.registerNewTeam("crewAFlags");
        crewAFlagsTeam.addEntry("§a§r");
        crewAFlagsTeam.setPrefix("Флаги: " + sessionCrewManager.getCrewFlagOwnersAsString(crewA));
        objective.getScore("§a§r").setScore(9);

        Team crewAScoreTeam = scoreboard.registerNewTeam("crewAScore");
        crewAScoreTeam.addEntry("§b§r");
        crewAScoreTeam.setPrefix("Очки: " + sessionCrewManager.getCrewScore(crewA));
        objective.getScore("§b§r").setScore(8);

        objective.getScore("§a§r ").setScore(7);

        objective.getScore("Команда " + crewB.getCrewName()).setScore(6);

        Team crewBFlagsTeam = scoreboard.registerNewTeam("crewBFlags");
        crewBFlagsTeam.addEntry("§c§r");
        crewBFlagsTeam.setPrefix("Флаги: " + sessionCrewManager.getCrewFlagOwnersAsString(crewB));
        objective.getScore("§c§r").setScore(5);

        Team crewBScoreTeam = scoreboard.registerNewTeam("crewBScore");
        crewBScoreTeam.addEntry("§d§r");
        crewBScoreTeam.setPrefix("Очки: " + sessionCrewManager.getCrewScore(crewB));
        objective.getScore("§d§r").setScore(4);

        objective.getScore("§b§r ").setScore(3);

        objective.getScore("Лимит очков: " + sessionCrewManager.getScoreLimit()).setScore(2);

        Team timeCounterTeam = scoreboard.registerNewTeam("timeCounter");
        timeCounterTeam.addEntry("§1§r");
        timeCounterTeam.setPrefix("Время: " + Utils.getTimeSinceValueAsString(sessionStartTime));
        objective.getScore("§1§r").setScore(1);
    }
}
