package me.cookiehunterrr.breadwars.listeners.abilities.passives;

import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityItemData;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class SpyFlagSteal implements Listener
{
    @EventHandler
    public void onPlayerGotHit(EntityDamageByEntityEvent e)
    {
        if (e.getEntity().getType() != EntityType.PLAYER) return;
        if (e.getDamager().getType() != EntityType.PLAYER) return;
        Player damager = (Player) e.getDamager();
        PlayerInfo damagerInfo = playerInfoManager.getPlayerInfo(damager.getUniqueId());
        if (damagerInfo.getJob() != Job.SPY) return;
        // Флаг нельзя украсть будучи невидимым
        if (damagerInfo.getClassAbility().ability == AbilityItemData.CLOCK_OF_INVISIBILITY)
        {
            if (damagerInfo.getClassAbility().info.taskID != 0) return;
        }
        try
        {
            if (damagerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        Player victim = (Player) e.getEntity();
        PlayerInfo victimInfo = playerInfoManager.getPlayerInfo(victim.getUniqueId());
        if (victimInfo.getCrew() == damagerInfo.getCrew()) return;
        // Есть ли у жертвы союзный флаг (который можно украсть)
        SessionCrewManager crewManager = victimInfo.getCrew().getCurrentGameSession().getCrewManager();
        // Если жертва имеет на себе флаг своей команды
        if (!crewManager.getCrewFlagOwners(victimInfo.getCrew()).contains(victim)) return;
        // Если атакующий не может носить вражеский флаг
        if (!crewManager.isAbleToCarryFlagType(damager, false)) return;
        crewManager.changeFlagOwner(victimInfo.getCrew(), victim, damager);
        victim.sendMessage("§4" + damager.getDisplayName() + "§3 украл ваш флаг");
        damager.sendMessage("§3Вы украли флаг у противника §4" + victim.getDisplayName());
        //if (BreadWars.isDebugMode()) Debug.showFlagOwners(assaulter, session);
        crewManager.getSession().getScoreboardManager().updateScoreboardsForPlayers();
    }
}
