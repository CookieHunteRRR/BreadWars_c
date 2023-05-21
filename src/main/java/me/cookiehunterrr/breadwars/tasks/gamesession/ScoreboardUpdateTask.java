package me.cookiehunterrr.breadwars.tasks.gamesession;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.scoreboard.CustomScoreboardManager;
import me.cookiehunterrr.breadwars.tasks.SessionTaskInfo;
import me.cookiehunterrr.breadwars.tasks.TaskType;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdateTask extends BukkitRunnable
{
    CustomScoreboardManager scoreboardManager;

    public ScoreboardUpdateTask(CustomScoreboardManager scoreboardManager, SessionCrewManager crewManager)
    {
        this.scoreboardManager = scoreboardManager;
    }

    @Override
    public void run()
    {
        scoreboardManager.updateScoreboardsForPlayers();
    }

    public SessionTaskInfo runTaskAndGetTaskInfo()
    {
        String taskName = "Обновление скорборда";
        String taskDesc = "Ежесекундное обновление скорборда";
        TaskType taskType = TaskType.ADMIN;
        //UUID taskOwner = null;
        int taskID = this.runTaskTimer(BreadWars.getInstance(), 0, 20).getTaskId();
        return new SessionTaskInfo(taskID, taskName, taskDesc, taskType, null);
    }
}
