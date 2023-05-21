package me.cookiehunterrr.breadwars.tasks.gamesession;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.tasks.SessionTaskInfo;
import me.cookiehunterrr.breadwars.tasks.TaskType;
import org.bukkit.scheduler.BukkitRunnable;

public class CrewReadyToPlayTask extends BukkitRunnable
{
    Crew initiator;
    Crew oppositeCrew;
    SessionCrewManager crewManager;

    public CrewReadyToPlayTask(Crew initiator, SessionCrewManager crewManager)
    {
        this.initiator = initiator;
        this.crewManager = crewManager;
        oppositeCrew = crewManager.getOppositeCrew(initiator);
    }

    public SessionTaskInfo runTaskAndGetTaskInfo()
    {
        String taskName = "Ожидание подтверждения начала игры";
        String taskDesc = "Команда " + initiator.getCrewName() + " готова к игре. Ожидание ответа " + oppositeCrew.getCrewName();
        TaskType taskType = TaskType.ADMIN;
        //UUID taskOwner = null;
        int taskID = this.runTaskTimer(BreadWars.getInstance(), 0, 600).getTaskId();
        return new SessionTaskInfo(taskID, taskName, taskDesc, taskType, null);
    }

    @Override
    public void run()
    {
        String message = "§3==================§r\n" + initiator.getCrewName() + " §6готовы§r\n" + oppositeCrew.getCrewName()
                + " §6не готовы\n§3Ожидание команды§r " + oppositeCrew.getCrewName() + "\n§3==================";
        initiator.getCrewLeader().sendMessage(message);
        oppositeCrew.getCrewLeader().sendMessage(message);
    }
}