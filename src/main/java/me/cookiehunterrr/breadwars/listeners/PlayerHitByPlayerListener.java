package me.cookiehunterrr.breadwars.listeners;

import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class PlayerHitByPlayerListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerGotHit(EntityDamageByEntityEvent e)
    {
        if (e.getEntity().getType() != EntityType.PLAYER) return;
        if (e.getDamager().getType() != EntityType.PLAYER) return;
        Player damager = (Player) e.getDamager();
        PlayerInfo damagerInfo = playerInfoManager.getPlayerInfo(damager.getUniqueId());
        try
        {
            if (damagerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        Player victim = (Player) e.getEntity();
        PlayerInfo victimInfo = playerInfoManager.getPlayerInfo(victim.getUniqueId());
        SessionCrewManager crewManager = victimInfo.getCrew().getCurrentGameSession().getCrewManager();
        // Если союзник бьет союзника
        if (victimInfo.getCrew() == damagerInfo.getCrew())
        {
            // Отменяем урон
            e.setCancelled(true);
            // Если союзник которого ударили не носит на себе флаг своей команды
            if (!crewManager.getCrewFlagOwners(victimInfo.getCrew()).contains(victim)) return;
            victim.sendMessage("§3Союзник §a" + damager.getDisplayName() + "§3 забрал ваш флаг");
            damager.sendMessage("§3Вы забрали флаг у союзника §a" + victim.getDisplayName());
            //if (BreadWars.isDebugMode()) Debug.showFlagOwners(assaulter, session);
            crewManager.getSession().getScoreboardManager().updateScoreboardsForPlayers();
        }
    }
}
