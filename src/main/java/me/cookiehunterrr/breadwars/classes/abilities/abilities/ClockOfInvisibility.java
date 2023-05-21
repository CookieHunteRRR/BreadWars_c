package me.cookiehunterrr.breadwars.classes.abilities.abilities;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.abilities.*;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.gamesession.GameSession;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import me.cookiehunterrr.breadwars.tasks.SessionTaskInfo;
import me.cookiehunterrr.breadwars.tasks.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class ClockOfInvisibility extends Ability implements Listener, IDeactivateable
{
    public ClockOfInvisibility()
    {
        abilityType = AbilityType.CLASS_ABILITY;
        abilityActivationButton = AbilityActivationButton.RIGHT_CLICK;
        requiredJob = Job.SPY;
        requiredItem = null;
        requiresBlockInteraction = false;
        cooldown = 10;
        BreadWars.registerEventsInClass(this);
    }

    @Override
    public void activationMessage(Player player)
    {
        player.sendMessage("Часы невидимости активированы");
    }

    @Override
    public void deactivationMessage(Player player)
    {
        player.sendMessage("Действие Часов невидимости закончилось");
    }

    @Override
    public void activate(Player player)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        AbilityUserData abilityUserData = playerInfo.getClassAbility().info;
        abilityUserData.lastActivation = System.currentTimeMillis();
        abilityUserData.isPreparing = true;
        // содержит y в axes[0] и угол в axes[1]
        final double[] axes = {0, 0};
        abilityUserData.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BreadWars.getInstance(), () -> {
            long timeSinceAbilityUse = (System.currentTimeMillis() - abilityUserData.lastActivation) / 1000;
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE,
                    player.getLocation().add(Math.cos(axes[1]), axes[0], Math.sin(axes[1])), 10);
            axes[0] += 0.2;
            axes[1] += 0.9;

            if (timeSinceAbilityUse >= 1)
            {
                playerInfo.getCrew().getCurrentGameSession().getTaskManager().cancelTask(abilityUserData.taskID);
                abilityUserData.taskID = 0;
                abilityUserData.isPreparing = false;
                activateInvisibility(player);
            }
        }, 0, 2);

        SessionTaskInfo taskInfo = new SessionTaskInfo(abilityUserData.taskID,
                "Партиклы Часов невидимости",
                player.getDisplayName() + " активировал этот эффект",
                TaskType.PARTICLE_SPAWNING, player.getUniqueId());
        playerInfo.getCrew().getCurrentGameSession().getTaskManager().registerTask(taskInfo);
    }

    @Override
    public void deactivate(Player player)
    {
        // Вызывается в случае преждевременного выхода из невидимости
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        GameSession gameSession = playerInfo.getCrew().getCurrentGameSession();
        Crew enemyCrew = gameSession.getCrewManager().getOppositeCrew(playerInfo.getCrew());
        AbilityUserData abilityUserData = playerInfo.getClassAbility().info;
        long timeSinceAbilityUse = (System.currentTimeMillis() - abilityUserData.lastActivation) / 1000;

        if (timeSinceAbilityUse < 1 && abilityUserData.isPreparing)
        {
            playerInfo.getCrew().getCurrentGameSession().getTaskManager().cancelTask(abilityUserData.taskID);
            abilityUserData.taskID = 0;
            abilityUserData.isPreparing = false;
            player.sendMessage("§aВы прервали активацию Часов невидимости");
        }
        else
        {
            player.sendMessage("§aВы преждевременно завершили действие Часов невидимости");
        }
        changePlayerVisibility(true, enemyCrew, player);
        playerInfo.getCrew().getCurrentGameSession().getTaskManager().cancelTask(abilityUserData.taskID);
        // преждевременное отключение - кд равно проведенному в невидимости времени
        abilityUserData.lastActivation = System.currentTimeMillis() - ((this.cooldown - timeSinceAbilityUse) * 1000);
        abilityUserData.taskID = 0;
    }

    void activateInvisibility(Player player)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        GameSession gameSession = playerInfo.getCrew().getCurrentGameSession();
        Crew enemyCrew = gameSession.getCrewManager().getOppositeCrew(playerInfo.getCrew());
        AbilityUserData abilityUserData = playerInfo.getClassAbility().info;
        abilityUserData.lastActivation = System.currentTimeMillis();
        abilityUserData.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BreadWars.getInstance(), () -> {
            long timeSinceAbilityUse = (System.currentTimeMillis() - abilityUserData.lastActivation) / 1000;
            if (timeSinceAbilityUse < 1)
            {
                activationMessage(player);
                changePlayerVisibility(false, enemyCrew, player);
                player.sendMessage("§7§oТеперь вы невидимы");
            }

            if (timeSinceAbilityUse >= this.cooldown)
            {
                changePlayerVisibility(true, enemyCrew, player);
                deactivationMessage(player);
                playerInfo.getCrew().getCurrentGameSession().getTaskManager().cancelTask(abilityUserData.taskID);
                // т.к. не преждевременное отключение - кд будет обычным
                abilityUserData.lastActivation = System.currentTimeMillis();
                abilityUserData.taskID = 0;
            }
        }, 0, 25);
        // Сделал 25 вместо 20, потому что сообщение о включении абилки отправлялось два раза

        SessionTaskInfo taskInfo = new SessionTaskInfo(abilityUserData.taskID,
                "Часы невидимости",
                player.getDisplayName() + " активировал этот эффект",
                TaskType.ABILITY, player.getUniqueId());
        playerInfo.getCrew().getCurrentGameSession().getTaskManager().registerTask(taskInfo);
    }

    // true - visible, false - invisible
    void changePlayerVisibility(boolean state, Crew affectedCrew, Player hiddenPlayer)
    {
        if (state)
        {
            for (Player player : affectedCrew.getCrewMembers())
                player.showPlayer(BreadWars.getInstance(), hiddenPlayer);
        }
        else
        {
            for (Player player : affectedCrew.getCrewMembers())
                player.hidePlayer(BreadWars.getInstance(), hiddenPlayer);
        }
    }

    /*
    // Имеет приоритет выше AbilityActivationListener
    @EventHandler(priority = EventPriority.LOW)
    public void handleClockOfInvisibility(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        if (playerInfo.getJob() == null) return;
        if (playerInfo.getJob() != this.requiredJob) return;
        if (playerInfo.getClassAbility().ability != AbilityItemData.CLOCK_OF_INVISIBILITY) return;
        if (playerInfo.getClassAbility().info.taskID == 0) return;

        boolean actionPossible = false;
        // Разрешенные взаимодействия
        try { actionPossible = e.getItem().getItemMeta().getDisplayName().equals(AbilityItemData.CLOCK_OF_INVISIBILITY.getAbilityName()); }
        catch (NullPointerException ex) { e.setCancelled(true); return; }

        if (!actionPossible) { e.setCancelled(true); return; } else { e.setCancelled(false); }

        // Преждевременный выход из невидимости
        // (потому что после проверок выше мы имеем пкм с часами в руках с активированной невидимостью)
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        deactivateEarly(player);
    }

     */

    @EventHandler
    public void cancelItemConsumingWhileInvisible(PlayerItemConsumeEvent e)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(e.getPlayer().getUniqueId());
        try
        {
            if (playerInfo.getClassAbility().ability != AbilityItemData.CLOCK_OF_INVISIBILITY) return;
            // если таск ид = 0, значит абилка выключена, если isPreparing, значит еще не включилась
            if (playerInfo.getClassAbility().info.taskID == 0 || playerInfo.getClassAbility().info.isPreparing)
                return;
        }
        catch (Exception ex) { return; }

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void cancelDamageWhileInvisible(EntityDamageByEntityEvent e)
    {
        if (e.getDamager().getType() != EntityType.PLAYER) return;
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(e.getDamager().getUniqueId());
        try
        {
            if (playerInfo.getClassAbility().ability != AbilityItemData.CLOCK_OF_INVISIBILITY) return;
            // если таск ид = 0, значит абилка выключена, если isPreparing, значит еще не включилась
            if (playerInfo.getClassAbility().info.taskID == 0 || playerInfo.getClassAbility().info.isPreparing)
                return;
        }
        catch (Exception ex) { return; }

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(e.getPlayer().getUniqueId());
        try
        {
            if (playerInfo.getClassAbility().ability != AbilityItemData.CLOCK_OF_INVISIBILITY) return;
            // если таск ид = 0, значит абилка выключена, если isPreparing, значит еще не включилась
            if (playerInfo.getClassAbility().info.taskID == 0 || playerInfo.getClassAbility().info.isPreparing)
                return;
        }
        catch (Exception ex) { return; }

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreakBlockWhileInvisible(BlockBreakEvent e)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(e.getPlayer().getUniqueId());
        try
        {
            if (playerInfo.getClassAbility().ability != AbilityItemData.CLOCK_OF_INVISIBILITY) return;
            // если таск ид = 0, значит абилка выключена, если isPreparing, значит еще не включилась
            if (playerInfo.getClassAbility().info.taskID == 0 || playerInfo.getClassAbility().info.isPreparing)
                return;
        }
        catch (Exception ex) { return; }

        e.setCancelled(true);
    }
}
