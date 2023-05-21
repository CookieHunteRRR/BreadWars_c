package me.cookiehunterrr.breadwars.classes.admin;

import me.cookiehunterrr.breadwars.classes.Utils;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityInstance;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class PlayerInfoAdmin
{
    public static void getPlayerInfo(Player player, String targetNick)
    {
        Player target = Bukkit.getPlayer(targetNick);
        if (target == null) { player.sendMessage("§cНеверный никнейм"); return; }
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(target.getUniqueId());

        StringBuilder sb = new StringBuilder();
        sb.append(target.getDisplayName()).append(" (Job: ");
        if (playerInfo.getJob() != null) sb.append(playerInfo.getJob().jobName).append(")").append("\n");
        else sb.append("not set)").append("\n");
        sb.append("Crew: ");
        if (playerInfo.getCrew() != null) sb.append(playerInfo.getCrew().getCrewName()).append("\n");
        sb.append("Current chat channel: ").append(playerInfo.getChatChannel().name()).append("\n");
        sb.append("Class ability: ");
        if (playerInfo.getClassAbility() == null)
        {
            sb.append("not set");
            player.sendMessage(sb.toString());
            return;
        }
        AbilityInstance abilityInstance = playerInfo.getClassAbility();
        sb.append(abilityInstance.ability.getAbilityName()).append("\n");
        sb.append("Last activation: ").append(abilityInstance.info.lastActivation);
        sb.append(" (").append(Utils.getTimeSinceValueAsString(abilityInstance.info.lastActivation)).append(" ago)\n");
        sb.append("taskID: ").append(abilityInstance.info.taskID).append(" | isPreparing: ").append(abilityInstance.info.isPreparing);
        player.sendMessage(sb.toString());
    }
}
