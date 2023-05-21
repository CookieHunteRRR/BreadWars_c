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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

//import static me.cookiehunterrr.abilitiestesting.AbilitiesTesting.currentClassAbilities;

public class Conqueror extends Ability
{
    public void activationMessage(Player player)
    {
        player.sendMessage("Завоеватель активирован");
    }

    public void deactivationMessage(Player player)
    {
        player.sendMessage("Действие Завоевателя закончилось");
    }

    public Conqueror()
    {
        abilityType = AbilityType.CLASS_ABILITY;
        abilityActivationButton = AbilityActivationButton.RIGHT_CLICK;
        requiredJob = Job.SCOUT;
        requiredItem = null;
        requiresBlockInteraction = false;
        cooldown = 20;
    }

    public void activate(Player player)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        AbilityUserData abilityUserData = playerInfo.getClassAbility().info;
        abilityUserData.lastActivation = System.currentTimeMillis();
        abilityUserData.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BreadWars.getInstance(), () -> {
            long timeSinceAbilityUse = (System.currentTimeMillis() - abilityUserData.lastActivation) / 1000;
            if (timeSinceAbilityUse < 1)
                activationMessage(player);

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 6));
            for (Player target : Utils.getNearbyPlayers(player, 10))
            {
                if (playerInfo.getCrew() == playerInfoManager.getPlayerInfo(target.getUniqueId()).getCrew())
                {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 6));
                }
            }
            if (timeSinceAbilityUse >= 10)
            {
                playerInfo.getCrew().getCurrentGameSession().getTaskManager().cancelTask(abilityUserData.taskID);
                deactivationMessage(player);
                abilityUserData.taskID = 0;
            }
        }, 0, 20L);

        SessionTaskInfo taskInfo = new SessionTaskInfo(abilityUserData.taskID,
                "Завоеватель",
                player.getDisplayName() + " активировал этот эффект",
                TaskType.ABILITY, player.getUniqueId());
        playerInfo.getCrew().getCurrentGameSession().getTaskManager().registerTask(taskInfo);
    }
}
