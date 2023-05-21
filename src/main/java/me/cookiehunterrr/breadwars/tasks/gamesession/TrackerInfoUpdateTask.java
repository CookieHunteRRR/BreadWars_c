package me.cookiehunterrr.breadwars.tasks.gamesession;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.Utils;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.tracker.TrackerManager;
import me.cookiehunterrr.breadwars.tasks.SessionTaskInfo;
import me.cookiehunterrr.breadwars.tasks.TaskType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class TrackerInfoUpdateTask extends BukkitRunnable
{
    TrackerManager trackerManager;
    SessionCrewManager crewManager;

    public TrackerInfoUpdateTask(TrackerManager trackerManager, SessionCrewManager crewManager)
    {
        this.trackerManager = trackerManager;
        this.crewManager = crewManager;
    }

    public SessionTaskInfo runTaskAndGetTaskInfo()
    {
        String taskName = "Обновление информации в трекере";
        String taskDesc = "Ежесекундное обновление информации трекера для игроков с трекером в руках";
        TaskType taskType = TaskType.ADMIN;
        int taskID = this.runTaskTimer(BreadWars.getInstance(), 0, 20).getTaskId();
        return new SessionTaskInfo(taskID, taskName, taskDesc, taskType, null);
    }

    void updateLodestoneAndSendInfo(Player whoNeedsUpdate, Crew whoNeedsUpdateCrew, Player target)
    {
        ItemStack tracker = whoNeedsUpdate.getInventory().getItem(TrackerManager.getReservedSlot());
        ItemMeta meta;
        try { meta = tracker.getItemMeta(); }
        catch (NullPointerException ex) { return; }
        // По идее это должно быть только в случае, если все вражеские флаги сворованы
        if (target == null)
        {
            Utils.sendMessageToActionBar(whoNeedsUpdate, "Трекер не может найти цель");
        }
        // Если враг украл флаг игрока, которому ищут цель
        else if (crewManager.getCrewFlagOwners(whoNeedsUpdateCrew).contains(target))
        {
            String message = "§c§n" + target.getDisplayName() + " | Расстояние: " +
                    String.format("%.2f", Utils.getDistanceBetweenLocations(whoNeedsUpdate.getLocation(), target.getLocation()));
            Utils.sendMessageToActionBar(whoNeedsUpdate, message);
        }
        // Если враг имеет свой флаг (обычная цель)
        else
        {
            String message = "§2" + target.getDisplayName() + " | Расстояние: " +
                    String.format("%.2f", Utils.getDistanceBetweenLocations(whoNeedsUpdate.getLocation(), target.getLocation()));
            Utils.sendMessageToActionBar(whoNeedsUpdate, message);
        }
        tracker.setItemMeta(meta);
    }

    @Override
    public void run()
    {
        for (Player player : new ArrayList<>(trackerManager.getCurrentlyHolding()))
        {
            // Если игрок больше не держит трекер
            if (player.getInventory().getHeldItemSlot() != TrackerManager.getReservedSlot())
            {
                trackerManager.removeFromCurrentlyHolding(player);
                continue;
            }
            Crew playerCrew = playerInfoManager.getPlayerInfo(player.getUniqueId()).getCrew();
            // Если нынешняя цель не самая приоритетная (не имеет флага команды игрока, которому ищут цель)
            if (!crewManager.getCrewFlagOwners(playerCrew).contains(trackerManager.getCurrentTarget(player)))
                trackerManager.setCurrentTarget(player, trackerManager.getAppropriateTarget(player));
            updateLodestoneAndSendInfo(player, playerCrew, trackerManager.getCurrentTarget(player));
        }
    }
}
