package me.cookiehunterrr.breadwars.tasks.gamesession;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.tasks.SessionTaskInfo;
import me.cookiehunterrr.breadwars.tasks.TaskType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FlagScoringTask extends BukkitRunnable
{
    final long scoringPeriod = 1200;
    final int scorePerPeriod = 2;
    SessionCrewManager crewManager;
    Crew crewA;
    Crew crewB;

    public FlagScoringTask(SessionCrewManager crewManager)
    {
        this.crewManager = crewManager;
        this.crewA = crewManager.getCrewA();
        this.crewB = crewManager.getCrewB();
    }

    public SessionTaskInfo runTaskAndGetTaskInfo()
    {
        String taskName = "Обновление очков";
        String taskDesc = "Ежеминутный подсчет очков за флаги у обеих команд";
        TaskType taskType = TaskType.ADMIN;
        //UUID taskOwner = null;
        int taskID = this.runTaskTimer(BreadWars.getInstance(), 0, scoringPeriod).getTaskId();
        return new SessionTaskInfo(taskID, taskName, taskDesc, taskType, null);
    }

    @Override
    public void run()
    {
        for (Player player : crewManager.getCrewFlagOwners(crewA))
            if (!crewA.getCrewMembers().contains(player)) crewManager.addCrewScore(crewB, scorePerPeriod);
        for (Player player : crewManager.getCrewFlagOwners(crewB))
            if (!crewB.getCrewMembers().contains(player)) crewManager.addCrewScore(crewA, scorePerPeriod);
    }
}
