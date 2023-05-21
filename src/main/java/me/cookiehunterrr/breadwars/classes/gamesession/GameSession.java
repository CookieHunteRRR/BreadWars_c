package me.cookiehunterrr.breadwars.classes.gamesession;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.Utils;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityItemData;
import me.cookiehunterrr.breadwars.classes.airdrop.AirdropItem;
import me.cookiehunterrr.breadwars.classes.airdrop.AirdropManager;
import me.cookiehunterrr.breadwars.classes.chat.ChatManager;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.customitems.CustomItemManager;
import me.cookiehunterrr.breadwars.classes.customitems.evo.EVOItem;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import me.cookiehunterrr.breadwars.classes.scoreboard.CustomScoreboardManager;
import me.cookiehunterrr.breadwars.classes.tracker.TrackerManager;
import me.cookiehunterrr.breadwars.tasks.SessionTaskManager;
import me.cookiehunterrr.breadwars.tasks.gamesession.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class GameSession
{
    SessionState sessionState;
    SessionTaskManager sessionTaskManager;
    SessionCrewManager sessionCrewManager;
    CustomScoreboardManager scoreboardManager;
    TrackerManager trackerManager;
    AirdropManager airdropManager;
    long whenGameStarted;

    public GameSession(Crew crewA, Crew crewB)
    {
        this.sessionState = SessionState.SESSION_AWAITING_RESPONSE;
        this.sessionTaskManager = new SessionTaskManager();
        this.sessionCrewManager = new SessionCrewManager(this, crewA, crewB);
        this.trackerManager = new TrackerManager(sessionCrewManager);
        this.airdropManager = new AirdropManager(sessionCrewManager);
        sessionTaskManager.registerTask(new AwaitingResponseTask(crewA, crewB).runTaskAndGetTaskInfo());
        whenGameStarted = 0;
    }

    public void deactivateSession()
    {
        sessionCrewManager.killSession();
    }

    public SessionState getSessionState() { return sessionState; }
    public SessionCrewManager getCrewManager() { return sessionCrewManager; }
    public SessionTaskManager getTaskManager() { return sessionTaskManager; }
    public CustomScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public TrackerManager getTrackerManager() { return trackerManager; }
    public AirdropManager getAirdropManager() { return airdropManager; }
    public long getWhenGameStarted() { return whenGameStarted; }
    public void addPlayerCurrentlyHoldingTracker(Player player) { trackerManager.addToCurrentlyHolding(player); }

    /*
    Изменение состояний игровой сессии
    */
    // В силу того, что (на момент версии 0.3) при создании сессии запускается только таск на уничтожение сессии в случае
    // не принятия противниками игры, при смене состояния сессии на FORMED можно просто отменять все таски в сессии
    public void changeStateToFormed()
    {
        sessionTaskManager.cancelAllTasks();
        sessionState = SessionState.SESSION_FORMED;
        sessionCrewManager.sendSessionFormedMessage();
    }

    public void changeStateToStarted()
    {
        sessionTaskManager.cancelAllTasks();
        Location[] locationsForCrews = findSafeLocationsForCrews();

        sessionState = SessionState.GAME_ACTIVE;
        whenGameStarted = System.currentTimeMillis();
        this.scoreboardManager = new CustomScoreboardManager(sessionCrewManager, whenGameStarted);
        sessionCrewManager.initializeDataOnGameStart();

        scoreboardManager.createInitialScoreboard();
        sessionTaskManager.registerTask(new ScoreboardUpdateTask(scoreboardManager, sessionCrewManager).runTaskAndGetTaskInfo());

        sessionCrewManager.sendSessionStartedMessage();

        for (Player player : sessionCrewManager.getAllPlayersInSession())
        {
            player.setFoodLevel(20);
            player.setHealth(20);
            player.setExp(0);
            player.getInventory().clear();
            ChatManager.setChatChannelToCrew(player);
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder center 0 0");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set 3000");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule announceAdvancements false");
        //createWorldBorderShrinkingTask();

        teleportPlayersToCorners(locationsForCrews[0], locationsForCrews[1]);
        trackerManager.setInitialTargets();
        givePlayersRequiredItems();
        sessionTaskManager.registerTask(new AirdropTask(airdropManager, sessionCrewManager).runTaskAndGetTaskInfo());
        sessionTaskManager.registerTask(new FlagScoringTask(sessionCrewManager).runTaskAndGetTaskInfo());
        sessionTaskManager.registerTask(new TrackerInfoUpdateTask(trackerManager, sessionCrewManager).runTaskAndGetTaskInfo());
    }

    public void changeStateToEnded(Crew winner)
    {
        sessionState = SessionState.GAME_ENDED;
        scoreboardManager.updateScoreboardsForPlayers();
        sessionTaskManager.cancelAllTasks();

        String message = "§aИГРА ОКОНЧЕНА\nПобедившая команда: " + winner.getCrewName() + "\nБольше добавить нечего вы прошли этот альфа-тест гг";
        for (Player player : sessionCrewManager.getAllPlayersInSession())
            player.sendMessage(message);
    }

    /*
    Методы, используемые для телепорта игроков по углам
    */
    void teleportPlayersToCorners(Location crewALocation, Location crewBLocation)
    {
        Crew crewA = getCrewManager().getCrewA();
        Crew crewB = getCrewManager().getCrewB();
        // Устанавливаем дефолтную точку респавна для команд
        sessionCrewManager.getCrewGameInfo(crewA).setRespawnLocation(crewALocation);
        sessionCrewManager.getCrewGameInfo(crewB).setRespawnLocation(crewBLocation);

        for (Player player : crewA.getCrewMembers())
        {
            player.teleport(crewALocation);
            player.setBedSpawnLocation(crewALocation, true);
        }
        for (Player player : crewB.getCrewMembers())
        {
            player.teleport(crewBLocation);
            player.setBedSpawnLocation(crewBLocation, true);
        }
    }

    Location[] findSafeLocationsForCrews()
    {
        Location[] loc = new Location[2];
        World world = sessionCrewManager.getCrewA().getCrewLeader().getWorld();

        // Находим локации для команд, если не можем найти безопасную локацию возвращаем нулл и там ошибку выдаем
        Location crewALocation = findSafeLocationInRange(world, -1499, -1000, 1000, 1499);
        if (crewALocation == null) {
            crewALocation = new Location(world, -1499, world.getHighestBlockYAt(-1499, 1499), 1499);
            System.out.println("[BreadWars GameSession] Couldn't find safe location for crewA");
        }

        loc[0] = crewALocation;

        Location crewBLocation = findSafeLocationInRange(world, 1000, 1499, -1499, -1000);
        if (crewBLocation == null)
        {
            crewBLocation = new Location(world, 1499, world.getHighestBlockYAt(1499, -1499), -1499);
            System.out.println("[BreadWars GameSession] Couldn't find safe location for crewB");
        }
        loc[1] = crewBLocation;

        return loc;
    }

    // Первая координата должна быть больше второй
    // Конечно можно было бы сделать по одной координате, которая сканирует до ближайшего угла, но это сложно
    Location findSafeLocationInRange(World w, int x1, int x2, int z1, int z2)
    {
        if (x1 > x2 || z1 > z2) throw new IllegalArgumentException("[BreadWars GameSession] First coordinate can't be" +
                " bigger than second in findSafeLocationInRange");

        for (; x1 < x2; x1++)
        {
            for (; z1 < z2; z1++)
            {
                Location locToCheck = new Location(w, x1, w.getHighestBlockYAt(x1, z1), z1);
                if (isLocationSafe(locToCheck))
                {
                    locToCheck.add(0, 1, 0);
                    return locToCheck;
                }
            }
        }
        return null;
    }
	
    boolean isLocationSafe(Location location)
    {
        Block feet = location.getBlock();
        if (!feet.getType().isTransparent() && !feet.getLocation().add(0, 1, 0).getBlock().getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block head = feet.getRelative(BlockFace.UP);
        if (!head.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block ground = feet.getRelative(BlockFace.DOWN);
        return ground.getType().isSolid(); // not solid
    }

    /*
    Методы, используемые для старта игры
    */
    void givePlayersRequiredItems()
    {
        for (Player player : getCrewManager().getAllPlayersInSession())
        {
            PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
            Inventory playerInv = player.getInventory();
            // Выдать игроку предмет-абилку
            playerInv.setItem(AbilityItemData.classAbilityReservedInventorySlot,
                    playerInfo.getClassAbility().ability.getAbilityAsItemStack());
            // Выдать игроку трекер
            playerInv.setItem(TrackerManager.getReservedSlot(), trackerManager.getTrackerForPlayer());
            // Выдать игроку эво предметы
            for (ItemStack itemToGive : getBasicEVOItems())
                playerInv.addItem(itemToGive);
            if (playerInfo.getJob() == Job.RANGER) playerInv.addItem(EVOItem.EVO_BOW.createEvoItem());
            // После выдачи всех соулбаунд шмоток обновить им лор, чтоб отображалось что они соулбаунд
            for (ItemStack item : playerInv.getStorageContents())
                if (item != null) CustomItemManager.setUpdatedItemMeta(item);
            // Выдать игроку стартер кит
            ArrayList<ItemStack> toGive = getKitForJob("all-jobs");
            toGive.addAll(getKitForJob(playerInfo.getJob().name().toLowerCase()));
            for (ItemStack itemToGive : toGive)
                playerInv.addItem(itemToGive);
        }
        //givePlayerAbilities();
        //giveInitialTrackers();
        //giveStarterKits();
    }

    ArrayList<ItemStack> getKitForJob(String jobName)
    {
        ArrayList<ItemStack> kit = new ArrayList<>();
        for (String s : BreadWars.getInstance().getConfig().getStringList(String.format("starter-kits.%s", jobName)))
        {
            String[] item = s.split(":");
            Material itemMaterial = Material.getMaterial(item[0].toUpperCase());
            assert itemMaterial != null;
            if (item.length > 1)
            {
                int itemAmount = Integer.parseInt(item[1]);
                // Значит только материал и количество
                if (item.length == 2) {
                    kit.add(new ItemStack(itemMaterial, itemAmount));
                    continue;
                }
                Map<Enchantment, Integer> enchantments = new HashMap<>();
                String[] enchantmentsRaw = item[2].split(",");
                for (String enchantmentRaw : enchantmentsRaw)
                {
                    String[] enchantment = enchantmentRaw.split("\\.");
                    enchantments.put(Utils.getEnchantmentByName(enchantment[0]),
                            Integer.parseInt(enchantment[1]));
                }
                ItemStack enchantedItem = new ItemStack(itemMaterial, itemAmount);
                enchantedItem.addEnchantments(enchantments);
                kit.add(enchantedItem);
                continue;
            }
            kit.add(new ItemStack(itemMaterial));
        }
        return kit;
    }

    ArrayList<ItemStack> getBasicEVOItems()
    {
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(EVOItem.EVO_SWORD.createEvoItem());
        items.add(EVOItem.EVO_PICKAXE.createEvoItem());
        items.add(EVOItem.EVO_AXE.createEvoItem());
        items.add(EVOItem.EVO_HELMET.createEvoItem());
        items.add(EVOItem.EVO_CHESTPLATE.createEvoItem());
        items.add(EVOItem.EVO_LEGGINGS.createEvoItem());
        items.add(EVOItem.EVO_BOOTS.createEvoItem());
        return items;
    }
}
