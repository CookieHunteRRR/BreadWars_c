package me.cookiehunterrr.breadwars.tasks;

import java.util.UUID;

public class SessionTaskInfo
{
    int taskID;
    String taskName;
    String taskDescription;
    TaskType taskType;
    UUID taskOwner;

    public SessionTaskInfo(int id, String name, String desc, TaskType type, UUID owner)
    {
        this.taskID = id;
        this.taskName = name;
        this.taskDescription = desc;
        this.taskType = type;
        this.taskOwner = owner;
    }

    public int getTaskID() { return taskID; }
    public String getTaskName() { return taskName; }
    public String getTaskDescription() { return taskDescription; }
    public TaskType getTaskType() { return taskType; }
    public UUID getTaskOwner() { return taskOwner; }
}