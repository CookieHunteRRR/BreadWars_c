package me.cookiehunterrr.breadwars.classes.airdrop;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.Constants;
import me.cookiehunterrr.breadwars.classes.Utils;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.crews.SessionCrewManager;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class AirdropManager
{
    // location - где дропнуся, int - тир
    Map<Location, Integer> registeredAirdropCrates;
    SessionCrewManager crewManager;

    public AirdropManager(SessionCrewManager crewManager)
    {
        this.registeredAirdropCrates = new HashMap<>();
        this.crewManager = crewManager;
    }

    public void addAirdrop(Location location, int tier)
    {
        registeredAirdropCrates.put(location, tier);
    }
    void removeAirdrop(Location location) { registeredAirdropCrates.remove(location); }

    public void createAirdropOnLocation(Location location, int tier)
    {
        World world = crewManager.getCrewA().getCrewLeader().getWorld();
        // Можно сюда потом партиклы красивые добавить
        world.getBlockAt(location).setType(Material.CHEST);
        addAirdrop(location, tier);
    }

    public boolean doesLocationHasAirdrop(Location location)
    {
        return registeredAirdropCrates.containsKey(location);
    }

    public void generateAirdropLoot(Inventory chestInventory, Player whoOpened)
    {
        Location chestLocation = chestInventory.getLocation();
        int chestTier = registeredAirdropCrates.get(chestLocation);
        Random random = BreadWars.getRandom();

        ArrayList<AirdropItem> lootTable = getLootTableBasedOnTier(chestTier);
        int sumOfWeights = getSumOfWeights(lootTable);
        int quantityToGenerate = Utils.getRandomIntegerInRange(Constants.minPossibleLootQuantity, Constants.maxPossibleLootQuantity);

        ItemStack[] lootToPut = new ItemStack[quantityToGenerate];
        for (int i = 0; i < quantityToGenerate; i++)
        {
            int number = random.nextInt(sumOfWeights);
            ItemStack itemToAdd = new ItemStack(Material.AIR);
            // Заменить на бинарный поиск если предметов станет оч много
            for (AirdropItem airdropItem : lootTable)
            {
                if (number <= airdropItem.weight)
                {
                    itemToAdd = airdropItem.item;
                    break;
                }
                number -= airdropItem.weight;
            }
            lootToPut[i] = itemToAdd;
        }

        chestInventory.setContents(lootToPut);
        // Завершив заполнять сундук шмотом, выдаем команде игрока очки за геймерство
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(whoOpened.getUniqueId());
        Crew playerCrew = playerInfo.getCrew();
        crewManager.addCrewScore(playerCrew, Constants.scorePerAirdrop);
        // Убираем аирдроп из списка
        removeAirdrop(chestLocation);
    }

    int getSumOfWeights(ArrayList<AirdropItem> lootTable)
    {
        int sum = 0;
        for (AirdropItem item : lootTable) sum += item.getWeight();
        return sum;
    }

    ArrayList<AirdropItem> getLootTableBasedOnTier(int tier)
    {
        ArrayList<AirdropItem> lootTable = new ArrayList<>();

        for (AirdropItem item : AirdropLootTableHolder.airdropLootTable)
        {
            // Если максимальный тир аирдропа при котором выпадает этот предмет меньше тира который мы проверяем, то предмета быть не должно
            if (item.getMaxAirdropTier() > 1)
                if (!(tier <= item.getMaxAirdropTier())) continue;
            // Если минимальный тир аирдропа больше того что мы проверяем, то этот предмет еще не в пуле
            if (!(tier >= item.getMinAirdropTier())) continue;
            lootTable.add(item);
        }

        return lootTable;
    }
}