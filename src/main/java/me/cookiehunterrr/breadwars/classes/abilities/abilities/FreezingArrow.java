package me.cookiehunterrr.breadwars.classes.abilities.abilities;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityActivationButton;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityItemData;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityType;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityUserData;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import me.cookiehunterrr.breadwars.tasks.SessionTaskInfo;
import me.cookiehunterrr.breadwars.tasks.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class FreezingArrow extends Ability implements Listener
{
    public void activationMessage(Player player)
    {
        player.sendMessage("Следующая выпущенная стрела будет замораживающей");
    }

    // У абилки может не быть деактивационного сообщения - вот пример такой абилки. Ну точнее оно будет отправляться не
    // кастеру абилки, а жертве
    public void deactivationMessage(Player player)
    {
        player.sendMessage("Действие заморозки закончилось");
    }

    public FreezingArrow()
    {
        abilityType = AbilityType.CLASS_ABILITY;
        abilityActivationButton = AbilityActivationButton.LEFT_CLICK;
        requiredJob = Job.RANGER;
        requiredItem = new RequiredMainHandItem(Material.BOW, null);
        requiresBlockInteraction = false;
        cooldown = 10;
        BreadWars.registerEventsInClass(this);
    }

    public void activate(Player player) {
        AbilityUserData abilityUserData = playerInfoManager.getPlayerInfo(player.getUniqueId()).getClassAbility().info;
        if (!abilityUserData.isPreparing)
        {
            abilityUserData.isPreparing = true;
            player.sendMessage("§3Следующая выпущенная вами стрела будет замораживающей");
        }
    }

    // Вызывается, когда замораживающая стрела попадает не в игрока
    private void deactivate(Player player)
    {
        AbilityUserData abilityUserData = playerInfoManager.getPlayerInfo(player.getUniqueId()).getClassAbility().info;
        abilityUserData.isPreparing = false;
        abilityUserData.lastActivation = System.currentTimeMillis();
    }

    // Вызывается, когда замораживающая стрела попадает в игрока
    private void applyEffects(Player whoInflicted, Player whoSuffered)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(whoInflicted.getUniqueId());
        AbilityUserData abilityUserData = playerInfo.getClassAbility().info;
        abilityUserData.isPreparing = false;
        abilityUserData.lastActivation = System.currentTimeMillis();
        abilityUserData.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BreadWars.getInstance(), () -> {
            long timeSinceAbilityUse = (System.currentTimeMillis() - abilityUserData.lastActivation) / 1000;
            if (timeSinceAbilityUse < 1)
                whoSuffered.sendMessage("§3Вы ощущаете пронизывающий холод");

            whoSuffered.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 10));
            whoSuffered.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 10));
            if (timeSinceAbilityUse >= 10)
            {
                removeEffects(whoSuffered);
                playerInfo.getCrew().getCurrentGameSession().getTaskManager().cancelTask(abilityUserData.taskID);
                abilityUserData.taskID = 0;
            }
        }, 0, 20L);

        SessionTaskInfo taskInfo = new SessionTaskInfo(abilityUserData.taskID,
                "Замораживающая стрела",
                whoInflicted.getDisplayName() + " наложил на " + whoSuffered.getDisplayName() + " этот эффект",
                TaskType.ABILITY, whoInflicted.getUniqueId());
        playerInfo.getCrew().getCurrentGameSession().getTaskManager().registerTask(taskInfo);
    }

    // Вызывается, когда действие замораживающей стрелы заканчивается
    private void removeEffects(Player whoSuffered)
    {
        whoSuffered.removePotionEffect(PotionEffectType.SLOW);
        whoSuffered.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        whoSuffered.sendMessage("§3Вы снова чувствуете тепло");
    }

    /*
    @EventHandler
    public void tryFreezingArrowActivation(EntityDamageByEntityEvent e)
    {
        if (e.getDamager().getType() != EntityType.ARROW) return;
        Arrow arrow = (Arrow) e.getDamager();
        if (arrow.getShooter() instanceof Player whoShot)
        {
            // Если у стрелка абилка не Freezing Arrow, то скип
            if (playerInfoManager.getPlayerInfo(whoShot.getUniqueId()).getClassAbility().ability != AbilityItemData.FREEZING_ARROW) return;
            AbilityUserData abilityInfo = playerInfoManager.getPlayerInfo(whoShot.getUniqueId()).getClassAbility().info;
            // return если жертва - союзник (нельзя союзника заморозить)
            // Это должна быть проверка на то, заряжена ли замораживающая стрела
            if (!abilityInfo.isPreparing) return;

            if (e.getEntity() instanceof Player whoSuffered)
                applyEffects(whoShot, whoSuffered);
            else deactivate(whoShot);
        }
    }

     */

    @EventHandler
    public void tryFreezingArrowActivation(ProjectileHitEvent e)
    {
        if (!(e.getEntity().getShooter() instanceof Player player)) return;
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        if (playerInfo.getJob() != Job.RANGER) return;
        if (playerInfo.getClassAbility().ability != AbilityItemData.FREEZING_ARROW) return;

        if (!playerInfo.getClassAbility().info.isPreparing) return;
        // Если прожектайл попал не по игроку - замораживающая стрела просрана
        if (!(e.getHitEntity() instanceof Player victim)) { deactivate(player); return; }
        // Если прожектайл попал по союзнику - замораживающая стрела просрана
        if (playerInfoManager.getPlayerInfo(victim.getUniqueId()).getCrew() == playerInfo.getCrew()) { deactivate(player); return; }
        applyEffects(player, victim);
    }
}
