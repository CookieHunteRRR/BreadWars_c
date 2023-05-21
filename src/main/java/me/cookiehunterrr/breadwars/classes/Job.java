package me.cookiehunterrr.breadwars.classes;

import me.cookiehunterrr.breadwars.classes.abilities.AbilityInstance;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityItemData;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityUserData;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public enum Job
{
    SCOUT("Разведчик", Material.FEATHER,
            "§3Захватывайте вражеские флаги, сталкиваясь с противниками лицом к лицу",
            "CONQUEROR"),
    RANGER("Стрелок", Material.BOW,
            "§3Осаждайте врагов дальнобойным оружием",
            "FREEZING_ARROW"),
    SUPPORT("Поддержка", Material.BOOK,
            "§3Оказывайте всяческую поддержку команде",
            "AURA_OF_HEALING"),
    SPY("Шпион", Material.CLOCK,
            "§3Скрытно проникайте в тыл врага,выдавая информацию команде",
            "CLOCK_OF_INVISIBILITY");

    public String jobName;
    public Material jobRepresentingItem;
    public String jobDescription;
    public String defaultJobAbilityName;

    Job(String name, Material representingItem, String desc, String abilityName)
    {
        this.jobName = name;
        this.jobRepresentingItem = representingItem;
        this.jobDescription = desc;
        this.defaultJobAbilityName = abilityName;
    }

    /*
    public static ArrayList<String> getAllJobNames()
    {
        ArrayList<String> list = new ArrayList<>();
        for (Job job : Job.values()) list.add(job.jobName);
        return list;
    } */

    public static Job getJobByName(String name)
    {
        for (Job job : Job.values())
        {
            if (job.jobName.equalsIgnoreCase(name)) return job;
        }
        return null;
    }

    public static void changePlayerJob(Player player, Job job)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        Crew playerCrew = playerInfo.getCrew();
        if (playerCrew == null)
        {
            player.sendMessage("§cВы должны состоять в команде чтобы выбирать класс");
            return;
        }
        if (playerCrew.isJobTaken(job))
        {
            player.sendMessage("§cВ вашей команде уже есть игрок с таким классом");
            return;
        }

        // Устанавливает класс и дефолтную абилку этого класса
        playerInfo.setJob(job);
        playerInfo.setClassAbility(new AbilityInstance(AbilityItemData.valueOf(job.defaultJobAbilityName), new AbilityUserData()));
        player.sendMessage("§aВы выбрали класс " + job.jobName);
    }
}
