package me.cookiehunterrr.breadwars.listeners.abilities.passives;

import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityItemData;
import me.cookiehunterrr.breadwars.classes.customitems.CustomResource;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import static me.cookiehunterrr.breadwars.BreadWars.customItemManager;
import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class RangerInfiniteArrows implements Listener
{
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent e)
    {
        if (!(e.getEntity() instanceof Player player)) return;
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        if (playerInfo.getJob() == null) return;
        if (playerInfo.getJob() != Job.RANGER) return;

        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        ItemStack spiritualArrow = customItemManager.getCustomItem(CustomResource.SPIRITUAL_ARROW);
        if (!e.getConsumable().getItemMeta().hasDisplayName()) return;
        if (spiritualArrow.getItemMeta().getDisplayName().equals(e.getConsumable().getItemMeta().getDisplayName()));
        {
            e.setConsumeItem(false);
            // обновлять инвентарь обязательно!!!!!!!!!!!!!!!
            player.updateInventory();
            //System.out.println(e.getConsumable().getItemMeta().getDisplayName() + " consumed");
        }
    }

    @EventHandler
    public void onPlayerItemSwitch(PlayerItemHeldEvent e)
    {
        Player player = e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        if (playerInfo.getJob() == null) return;
        if (playerInfo.getJob() != Job.RANGER) return;

        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        ItemStack newItem = player.getInventory().getItem(e.getNewSlot());
        ItemStack oldItem = player.getInventory().getItem(e.getPreviousSlot());
        if (oldItem != null)
        {
            if (oldItem.getType() == Material.BOW || oldItem.getType() == Material.CROSSBOW)
                player.getInventory().setItem(AbilityItemData.classAbilityReservedInventorySlot,
                        playerInfo.getClassAbility().ability.getAbilityAsItemStack());
        }
        if (newItem != null)
        {
            if (newItem.getType() == Material.BOW || newItem.getType() == Material.CROSSBOW)
                player.getInventory().setItem(AbilityItemData.classAbilityReservedInventorySlot,
                        customItemManager.getCustomItem(CustomResource.SPIRITUAL_ARROW));
        }
    }
}
