package me.cookiehunterrr.breadwars.commands;

import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class JobCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player player)
        {
            if (args.length < 1)
            {
                sendHelpMessage(player);
                return true;
            }

            PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
            if (playerInfo.getCrew() == null)
            {
                player.sendMessage("§cВы должны состоять в команде, чтобы использовать эту команду");
                return true;
            }
            String subcommand = args[0].toLowerCase();
            // Ограничение возможных команд во время игровой сессии
            if (playerInfo.getCrew().getCurrentGameSession() != null)
            {
                if (playerInfo.getCrew().getCurrentGameSession().getSessionState() == SessionState.GAME_ACTIVE)
                {
                    // Перечисление доступных команд
                    if (!subcommand.equals("help"))
                    {
                        player.sendMessage("§cВы не можете использовать эту команду во время активной игровой сессии");
                        return true;
                    }
                }
            }

            switch (subcommand)
            {
                case "help" -> sendHelpMessage(player);

                case "list" -> player.openInventory(createJobList());

                case "choose" -> {
                    if (args.length != 2)
                    {
                        player.sendMessage("§cВы не указали класс, либо указали избыточное количество аргументов");
                        return true;
                    }
                    Job jobToGet = Job.getJobByName(args[1]);
                    if (jobToGet == null)
                    {
                        player.sendMessage("§cВы указали несуществующий класс");
                        return true;
                    }
                    Job.changePlayerJob(player, jobToGet);
                }

                default -> {
                    player.sendMessage("§cНеизвестный аргумент");
                    sendHelpMessage(player);
                }
            }
        }
        return false;
    }

    private Inventory createJobList()
    {
        Inventory inv = Bukkit.createInventory(null, 9, "Классы");
        for (Job job : Job.values()) inv.addItem(createJobItem(job));
        return inv;
    }

    private ItemStack createJobItem(Job job)
    {
        ItemStack jobItem = new ItemStack(job.jobRepresentingItem);
        ItemMeta meta = jobItem.getItemMeta();
        meta.setDisplayName("§r" + job.jobName);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(job.jobDescription);
        meta.setLore(lore);
        jobItem.setItemMeta(meta);

        return jobItem;
    }

    void sendHelpMessage(Player player)
    {
        player.sendMessage("""
                §3=== Команда job ===
                §a/job help§r - вызов данного сообщения
                §a/job list§r - выдает список существующих классов
                §a/job choose§r <название> - присваивает выбранный класс""");
    }
}
