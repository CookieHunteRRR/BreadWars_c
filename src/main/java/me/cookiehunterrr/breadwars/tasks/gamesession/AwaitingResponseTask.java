package me.cookiehunterrr.breadwars.tasks.gamesession;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.tasks.SessionTaskInfo;
import me.cookiehunterrr.breadwars.tasks.TaskType;
import org.bukkit.scheduler.BukkitRunnable;

// Активируется при создании новой игровой сессии
// Отменяется при переходе в следующий SessionState
public class AwaitingResponseTask extends BukkitRunnable
{
    Crew crewA;
    Crew crewB;

    public AwaitingResponseTask(Crew crewA, Crew crewB)
    {
        this.crewA = crewA;
        this.crewB = crewB;
    }

    public SessionTaskInfo runTaskAndGetTaskInfo()
    {
        String taskName = "Ожидание ответа вражеской команды";
        String taskDesc = "Ожидание ответа на вызов на бой с командой " + crewB.getCrewName() +
                " от команды " + crewA.getCrewName();
        TaskType taskType = TaskType.ADMIN;
        //UUID taskOwner = null;
        int taskID = this.runTaskLater(BreadWars.getInstance(), 200).getTaskId();
        return new SessionTaskInfo(taskID, taskName, taskDesc, taskType, null);
    }

    @Override
    public void run()
    {
        crewA.getCrewLeader().sendMessage("§cПротивник не принял ваш вызов");
        crewB.getCrewLeader().sendMessage("§cВремя ожидания вашего ответа на вызов истекло");
        BreadWars.gameSessionManager.removeSession(crewA.getCurrentGameSession());
        crewA.setCurrentGameSession(null);
        crewB.setCurrentGameSession(null);
    }
}
