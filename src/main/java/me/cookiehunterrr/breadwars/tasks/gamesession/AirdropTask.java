package me.cookiehunterrr.breadwars.tasks.gamesession;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.Constants;
import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.Utils;
import me.cookiehunterrr.breadwars.classes.airdrop.AirdropManager;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import me.cookiehunterrr.breadwars.tasks.SessionTaskInfo;
import me.cookiehunterrr.breadwars.tasks.TaskType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class AirdropTask extends BukkitRunnable
{
    AirdropManager airdropManager;
    SessionCrewManager crewManager;
    int taskIterations = 0;
    boolean isAirdropPreparing = false;
    Location locForAirdrop = null;
    Location[] locForSupportAirdrop = new Location[]{null, null};
    Player[] supports = new Player[]{null, null};

    public AirdropTask(AirdropManager airdropManager, SessionCrewManager crewManager)
    {
        this.airdropManager = airdropManager;
        this.crewManager = crewManager;
        for (Player player : crewManager.getCrewA().getCrewMembers())
        {
            PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
            if (playerInfo.getJob() == Job.SUPPORT) supports[0] = player;
        }
        for (Player player : crewManager.getCrewB().getCrewMembers())
        {
            PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
            if (playerInfo.getJob() == Job.SUPPORT) supports[1] = player;
        }
    }

    public SessionTaskInfo runTaskAndGetTaskInfo()
    {
        String taskName = "Генерация аирдропов";
        String taskDesc = "похуй";
        TaskType taskType = TaskType.ADMIN;
        //UUID taskOwner = null;
        int taskID = this.runTaskTimer(BreadWars.getInstance(), 1200, 1200).getTaskId();
        return new SessionTaskInfo(taskID, taskName, taskDesc, taskType, null);
    }

    @Override
    public void run()
    {
        taskIterations++;
        // Код идет дальше только если соблюдаются условия
        // Либо минута делится на 5 без остатка, либо готовится аирдроп
        if (taskIterations % 5 != 0 && !(isAirdropPreparing)) return;
        // taskIterations % 5 != 0 - true | !(isAirdropPreparing) - false -> taskIterations % 5 != 0 и isAirdropPreparing == true - 6-9 минуты
        // taskIterations % 5 != 0 - false | !(isAirdropPreparing) - true -> taskIterations % 5 == 0 и isAirdropPreparing == false - пятая минута
        // taskIterations % 5 != 0 - false | !(isAirdropPreparing) - false -> taskIterations % 5 == 0 и isAirdropPreparing == true - десятая минута
        // Для 6-9 минут
        if (taskIterations % 5 != 0)
        {
            sendAirdropAnnouncement(false);
            sendSupportAirdropAnnouncement(false);
            return;
        }
        // Для пятой минуты
        if (taskIterations % 10 != 0)
        {
            isAirdropPreparing = true;
            locForAirdrop = generateLocation();
            locForSupportAirdrop = generateLocationForSupports();
            sendAirdropAnnouncement(false);
            sendSupportAirdropAnnouncement(false);
            return;
        }
        // Для 10 минуты
        sendAirdropAnnouncement(true);
        sendSupportAirdropAnnouncement(true);
        airdropManager.createAirdropOnLocation(locForAirdrop, taskIterations / 10);
        if (locForSupportAirdrop[0] != null)
            airdropManager.createAirdropOnLocation(locForSupportAirdrop[0], taskIterations / 10);
        if (locForSupportAirdrop[1] != null)
            airdropManager.createAirdropOnLocation(locForSupportAirdrop[1], taskIterations / 10);
        isAirdropPreparing = false;
        locForAirdrop = null;
        locForSupportAirdrop = new Location[]{null, null};
    }

    Location generateLocation()
    {
        World world = crewManager.getCrewA().getCrewLeader().getWorld();
        int x = Utils.getRandomIntegerInRange(-Constants.airdropRadius, Constants.airdropRadius);
        int z = Utils.getRandomIntegerInRange(-Constants.airdropRadius, Constants.airdropRadius);
        return world.getHighestBlockAt(x, z).getLocation().add(0, 1, 0);
    }

    Location[] generateLocationForSupports()
    {
        Location[] locations = new Location[]{null, null};
        World world = crewManager.getCrewA().getCrewLeader().getWorld();
        if (supports[0] != null)
        {
            Location supportLocation = supports[0].getLocation();
            locations[0] = world.getHighestBlockAt(supportLocation.getBlockX(), supportLocation.getBlockZ()).getLocation().add(0, 1, 0);
        }
        if (supports[1] != null)
        {
            Location supportLocation = supports[1].getLocation();
            locations[1] = world.getHighestBlockAt(supportLocation.getBlockX(), supportLocation.getBlockZ()).getLocation().add(0, 1, 0);
        }
        return locations;
    }

    void sendAirdropAnnouncement(boolean isDroppingNow)
    {
        String location = "(" + locForAirdrop.getX() + ", " + locForAirdrop.getY() + ", " + locForAirdrop.getZ() + ")";
        if (!isDroppingNow)
        {
            int time = 10 - (taskIterations - ((taskIterations / 10) * 10));
            for (Player player : crewManager.getAllPlayersInSession())
                player.sendMessage(String.format("§eАирдроп выпадет на координатах %s через %d минут",
                        location, time));
        }
        else
        {
            for (Player player : crewManager.getAllPlayersInSession())
                player.sendMessage(String.format("§eАирдроп выпал на координатах %s", location));
        }
    }

    void sendSupportAirdropAnnouncement(boolean isDroppingNow)
    {
        if (!isDroppingNow)
        {
            int time = 10 - (taskIterations - ((taskIterations / 10) * 10));
            for (int i = 0; i < 2; i++)
            {
                if (supports[i] == null) continue;
                String location = "(" + locForSupportAirdrop[i].getX() + ", " + locForSupportAirdrop[i].getY() + ", " + locForSupportAirdrop[i].getZ() + ")";
                supports[i].sendMessage(String.format("§aДополнительный аирдроп выпадет на координатах %s через %d минут",
                        location, time));
            }
        }
        else
        {
            for (int i = 0; i < 2; i++)
            {
                if (supports[i] == null) continue;
                String location = "(" + locForSupportAirdrop[i].getX() + ", " + locForSupportAirdrop[i].getY() + ", " + locForSupportAirdrop[i].getZ() + ")";
                supports[i].sendMessage(String.format("§aАирдроп выпал на координатах %s", location));
            }
        }
    }
}
