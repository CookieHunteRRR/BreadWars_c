package me.cookiehunterrr.breadwars;

import me.cookiehunterrr.breadwars.classes.crews.CrewManager;
import me.cookiehunterrr.breadwars.classes.customitems.CustomItemManager;
import me.cookiehunterrr.breadwars.classes.gamesession.GameSessionManager;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfoManager;
import me.cookiehunterrr.breadwars.commands.*;
import me.cookiehunterrr.breadwars.listeners.*;
import me.cookiehunterrr.breadwars.listeners.abilities.AbilityActivationListener;
import me.cookiehunterrr.breadwars.listeners.abilities.passives.RangerInfiniteArrows;
import me.cookiehunterrr.breadwars.listeners.abilities.passives.ScoutHorseStatBoosting;
import me.cookiehunterrr.breadwars.listeners.abilities.passives.SpyFlagSteal;
import me.cookiehunterrr.breadwars.listeners.airdrop.AirdropOpenListener;
import me.cookiehunterrr.breadwars.listeners.attributes.ItemAttributeHandler;
import me.cookiehunterrr.breadwars.listeners.items.EVOHandler;
import me.cookiehunterrr.breadwars.listeners.tracker.TrackerHeldListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class BreadWars extends JavaPlugin
{
    private static BreadWars instance;
    private static Random random;
    public static GameSessionManager gameSessionManager;
    public static CrewManager crewManager;
    public static PlayerInfoManager playerInfoManager;
    public static CustomItemManager customItemManager;
    public static ArrayList<UUID> adminUUIDs;

    @Override
    public void onEnable()
    {
        instance = this;
        random = new Random();
        gameSessionManager = new GameSessionManager();
        crewManager = new CrewManager();
        playerInfoManager = new PlayerInfoManager();
        customItemManager = new CustomItemManager();
        adminUUIDs = new ArrayList<>();

        adminUUIDs.add(UUID.fromString("08b1979b-7fba-3187-a4d0-cba7c9309678"));

        // Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Listeners (он опять забудет добавить новый листенер)
        registerAllListeners();

        // Commands
        getCommand("crew").setExecutor(new CrewCommand());
        getCommand("admin").setExecutor(new AdminCommand());
        getCommand("job").setExecutor(new JobCommand());
        getCommand("game").setExecutor(new GameCommand());
        getCommand("swapchannel").setExecutor(new ChatCommand());

        if (getServer().getOnlinePlayers().size() > 0)
            registerPlayerInfoAfterReload();
    }

    void registerAllListeners()
    {
        // abilities
        registerEventsInClass(new AbilityActivationListener());
        registerEventsInClass(new RangerInfiniteArrows());
        registerEventsInClass(new ScoutHorseStatBoosting());
        registerEventsInClass(new SpyFlagSteal());
        // airdrop
        registerEventsInClass(new AirdropOpenListener());
        // attributes
        registerEventsInClass(new ItemAttributeHandler());
        // items
        registerEventsInClass(new EVOHandler());
        // tracker
        registerEventsInClass(new TrackerHeldListener());
        // general
        registerEventsInClass(new BlockBreakListener());
        registerEventsInClass(new BlockPlaceListener());
        registerEventsInClass(new ChatListener());
        registerEventsInClass(new JoinAndQuitHandler());
        registerEventsInClass(new PlayerDeathListener());
        registerEventsInClass(new PlayerHitByPlayerListener());
        registerEventsInClass(new PlayerRespawnHandler());
    }

    void registerPlayerInfoAfterReload()
    {
        int count = 0;
        for (Object obj : getServer().getOnlinePlayers().toArray())
        {
            Player player = (Player) obj;
            playerInfoManager.registerPlayer(player);
            count++;
        }
        sendLog("Registered " + count + " playerInfos on reload");
    }

    public static void registerEventsInClass(Listener listener)
    {
        getInstance().getServer().getPluginManager().registerEvents(listener, getInstance());
    }

    public void sendLog(String message)
    {
        this.getLogger().info(message);
    }

    public static BreadWars getInstance() { return instance; }
    public static Random getRandom() { return random; }
}