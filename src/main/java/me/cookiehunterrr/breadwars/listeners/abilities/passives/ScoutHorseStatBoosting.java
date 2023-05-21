package me.cookiehunterrr.breadwars.listeners.abilities.passives;

import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class ScoutHorseStatBoosting implements Listener
{
    @EventHandler
    public void onHorseMount(EntityMountEvent e)
    {
        if (!(e.getMount() instanceof Horse)) return;
        Horse horse = (Horse) e.getMount();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(e.getEntity().getUniqueId());
        if (playerInfo.getJob() == null) return;
        if (playerInfo.getJob() != Job.SCOUT) return;

        // Проверяем, садится ли скаут на лошадь в запущенной игре
        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        horse.setDomestication(horse.getMaxDomestication());
        horse.setJumpStrength(horse.getJumpStrength() + 0.5);
        AttributeInstance horseSpeed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        horseSpeed.setBaseValue(horseSpeed.getBaseValue() + 0.3);
    }

    @EventHandler
    public void onHorseDismount(EntityDismountEvent e)
    {
        if (!(e.getDismounted() instanceof Horse)) return;
        Horse horse = (Horse) e.getDismounted();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(e.getEntity().getUniqueId());
        if (playerInfo.getJob() == null) return;
        if (playerInfo.getJob() != Job.SCOUT) return;

        // Проверяем, слазит ли скаут с лошади в запущенной игре
        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        horse.setJumpStrength(horse.getJumpStrength() - 0.5);
        AttributeInstance horseSpeed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        horseSpeed.setBaseValue(horseSpeed.getBaseValue() - 0.3);
    }
}
