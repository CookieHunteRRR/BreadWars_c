package me.cookiehunterrr.breadwars.classes.abilities.abilities;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.Utils;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityActivationButton;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityType;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityUserData;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import me.cookiehunterrr.breadwars.tasks.SessionTaskInfo;
import me.cookiehunterrr.breadwars.tasks.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class AuraOfHealing extends Ability
{
    final double healPower;

    public AuraOfHealing()
    {
        abilityType = AbilityType.CLASS_ABILITY;
        abilityActivationButton = AbilityActivationButton.RIGHT_CLICK;
        requiredJob = Job.SUPPORT;
        requiredItem = null;
        requiresBlockInteraction = false;
        cooldown = 20;

        healPower = 1.5;
    }

    @Override
    public void activationMessage(Player player)
    {
        player.sendMessage("Аура лечения активирована");
    }

    @Override
    public void deactivationMessage(Player player)
    {
        player.sendMessage("Действие Ауры лечения закончилось");
    }

    @Override
    public void activate(Player player)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        AbilityUserData abilityUserData = playerInfo.getClassAbility().info;
        abilityUserData.lastActivation = System.currentTimeMillis();
        abilityUserData.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BreadWars.getInstance(), () -> {
            long timeSinceAbilityUse = (System.currentTimeMillis() - abilityUserData.lastActivation) / 1000;
            if (timeSinceAbilityUse < 1)
                activationMessage(player);

            Utils.changePlayerHealth(player, "heal", healPower);
            for (Player target : Utils.getNearbyPlayers(player, 5))
            {
                if (playerInfo.getCrew() == playerInfoManager.getPlayerInfo(target.getUniqueId()).getCrew())
                {
                    Utils.changePlayerHealth(target, "heal", healPower);
                }
            }
            if (timeSinceAbilityUse >= 10)
            {
                deactivationMessage(player);
                playerInfo.getCrew().getCurrentGameSession().getTaskManager().cancelTask(abilityUserData.taskID);
                abilityUserData.taskID = 0;
            }
        }, 0, 20);

        SessionTaskInfo taskInfo = new SessionTaskInfo(abilityUserData.taskID,
                "Аура лечения",
                player.getDisplayName() + " активировал этот эффект",
                TaskType.ABILITY, player.getUniqueId());
        playerInfo.getCrew().getCurrentGameSession().getTaskManager().registerTask(taskInfo);
    }
}
