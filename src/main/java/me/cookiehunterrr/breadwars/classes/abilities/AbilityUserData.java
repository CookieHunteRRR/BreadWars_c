package me.cookiehunterrr.breadwars.classes.abilities;

import org.bukkit.entity.Player;

import java.util.UUID;

public class AbilityUserData
{
    public UUID userUUID;
    public long lastActivation;
    public int taskID;
    public boolean isPreparing;

    public AbilityUserData()
    {
        lastActivation = System.currentTimeMillis() - 100000;
        taskID = 0;
        isPreparing = false;
    }

    public void setUserUUID(Player player)
    {
        userUUID = player.getUniqueId();
    }
}
