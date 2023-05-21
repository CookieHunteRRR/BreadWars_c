package me.cookiehunterrr.breadwars.classes.crews;

import me.cookiehunterrr.breadwars.classes.Job;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

import static me.cookiehunterrr.breadwars.BreadWars.crewManager;
import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

// Класс, отвечающий за зарегистрированные на сервере команды игроков
public class CrewManager
{
    ArrayList<Crew> registeredCrews;

    public CrewManager()
    {
        this.registeredCrews = new ArrayList<>();
    }

    // Метод для создания и регистрации команды на сервере
    public void registerCrew(Player player, String crewName)
    {
        if (playerInfoManager.getPlayerInfo(player.getUniqueId()).getCrew() != null)
        {
            player.sendMessage("§cВы не можете создавать команду, находясь в команде");
            return;
        }
        if (getCrewCount() >= 27)
        {
            player.sendMessage("§cДостигнуто максимальное количество команд");
            System.out.println("[BreadWars Crews] Total crews limit reached");
            return;
        }

        if (crewManager.isCrewNameOccupied(crewName))
        {
            player.sendMessage("§cТакое название команды уже занято");
            return;
        }

        registeredCrews.add(new Crew(crewName, player));
        player.sendMessage("§aУспешно создана команда " + crewName);
    }

    // Метод для удаления команды
    public void deleteCrew(Crew crew)
    {
        for (Object crewMember : crew.crewMembers.toArray())
        {
            Player player = (Player) crewMember;
            crew.removePlayer(player);
            player.sendMessage("Ваша команда была распущена лидером");
        }
        registeredCrews.remove(crew);
    }

    // Метод для вступления в команду
    public void joinCrew(Player player, String crewName)
    {
        if (playerInfoManager.getPlayerInfo(player.getUniqueId()).getCrew() != null)
        {
            player.sendMessage("§cВы уже состоите в команде");
            return;
        }
        Crew crew = getCrewByName(crewName);
        if (crew == null)
        {
            player.sendMessage("§cВы указали несуществующее название команды");
            return;
        }
        if (crew.addPlayer(player)) player.sendMessage("§aВы вступили в команду " + crew.crewName);
        else player.sendMessage("§cВ команде нет свободных слотов");
    }

    // Метод для покидания команды
    public void leaveCrew(Player player)
    {
        Crew playerCrew = playerInfoManager.getPlayerInfo(player.getUniqueId()).getCrew();
        if (playerCrew == null)
        {
            player.sendMessage("§cВы не состоите в команде");
            return;
        }
        if (playerCrew.crewMembers.size() == 1)
        {
            playerCrew.removePlayerWithNotification(player);
            registeredCrews.remove(playerCrew);
            player.sendMessage("§3Команда была распущена, так как вы были последним ее участником");
            return;
        }
        if (playerCrew.crewLeader == player)
        {
            for (Player crewMember : playerCrew.crewMembers)
            {
                if (crewMember != player)
                {
                    playerCrew.changeLeader(player, crewMember);
                    playerCrew.removePlayerWithNotification(player);
                    player.sendMessage("§aВы покинули команду " +
                            playerCrew.crewName +
                            ". Лидерство перешло к игроку "
                            + crewMember.getDisplayName());
                    crewMember.sendMessage("§aВы стали новым лидером команды " +
                            playerCrew.crewName + ", так как предыдущий лидер " + player.getDisplayName() +
                            " покинул команду");
                    return;
                }
            }
        }

        for (Player crewmate : playerCrew.getCrewMembers())
        {
            if (crewmate == player) { player.sendMessage("§aВы покинули команду " + playerCrew.crewName); continue; }
            crewmate.sendMessage("§a[Crew] " + player.getDisplayName() + " покинул команду");
        }
        playerCrew.removePlayer(player);
    }

    public Inventory outputCrewsToInventory()
    {
        Inventory crewList = Bukkit.createInventory(null, 27, "Список команд");
        for (Crew crew : registeredCrews) crewList.addItem(createCrewItem(crew));
        return crewList;
    }

    private ItemStack createCrewItem(Crew crew)
    {
        ItemStack crewItem = new ItemStack(Material.WHITE_BANNER);
        ItemMeta meta = crewItem.getItemMeta();
        meta.setDisplayName("§r" + crew.crewName);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§aЛидер команды: §f" + crew.crewLeader.getDisplayName());
        lore.add("§aСостав команды:");
        for (Player crewMember : crew.crewMembers)
        {
            String playerJobAsString = "";
            Job playerJob = playerInfoManager.getPlayerInfo(crewMember.getUniqueId()).getJob();
            if (playerJob != null)
                playerJobAsString = " [§3" + playerJob.jobName + "§f]";
            lore.add("§f- " + crewMember.getDisplayName() + playerJobAsString);
        }
        meta.setLore(lore);
        crewItem.setItemMeta(meta);
        return crewItem;
    }

    public int getCrewCount() { return registeredCrews.size(); }

    public Crew getCrewByName(String name)
    {
        for (Crew crew : registeredCrews)
            if (Objects.equals(crew.crewName, name)) return crew;
        return null;
    }

    public boolean isCrewNameOccupied(String name)
    {
        for (Crew crew : registeredCrews)
            if (Objects.equals(crew.crewName, name)) return true;
        return false;
    }
}
