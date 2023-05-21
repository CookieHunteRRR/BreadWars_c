package me.cookiehunterrr.breadwars.tasks;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Класс, контролирующий взаимодействие с тасками в игровой сессии
public class SessionTaskManager
{
    private Map<Integer, SessionTaskInfo> taskMap;

    public SessionTaskManager()
    {
        this.taskMap = new HashMap<>();
    }
    public int getActiveTasksCount() { return taskMap.size(); }
    public void registerTask(SessionTaskInfo taskInfo)
    {
        taskMap.put(taskInfo.getTaskID(), taskInfo);
    }
    public void cancelTask(int id)
    {
        Bukkit.getScheduler().cancelTask(id);
        taskMap.remove(id);
    }

    // Если у одного игрока будет несколько спавнов партиклов - все очень плохо
    public void cancelParticleSpawnForPlayer(UUID playerUUID)
    {
        for (SessionTaskInfo taskInfo : taskMap.values())
        {
            if (taskInfo.taskOwner != playerUUID) continue;
            if (taskInfo.taskType != TaskType.PARTICLE_SPAWNING) continue;
            cancelTask(taskInfo.taskID);
            return;
        }
    }

    public void cancelAllTasks()
    {
        for (int key : new ArrayList<>(taskMap.keySet()))
            cancelTask(key);
    }
}
