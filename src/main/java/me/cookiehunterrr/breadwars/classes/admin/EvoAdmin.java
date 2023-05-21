package me.cookiehunterrr.breadwars.classes.admin;

import me.cookiehunterrr.breadwars.classes.customitems.CustomAttribute;
import me.cookiehunterrr.breadwars.classes.customitems.evo.EVOItem;
import me.cookiehunterrr.breadwars.classes.customitems.evo.EvoDataType;
import me.cookiehunterrr.breadwars.classes.customitems.evo.EvoItemInformation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class EvoAdmin
{
    public static void getEvoInfo(Player player, ItemStack item)
    {
        if (item == null)
        {
            player.sendMessage("§cВ руке нет предмета");
            return;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (!(dataContainer.has(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType())))
        {
            player.sendMessage("§cПредмет не является ЭВО предметом");
            return;
        }
        EvoItemInformation evoInfo = dataContainer.get(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType());
        StringBuilder sb = new StringBuilder();
        EVOItem evoItem = EVOItem.getByOrdinal(evoInfo.getEvoID());
        sb.append("EVO ID: ").append(evoInfo.getEvoID()).append("\n");
        sb.append("Stage: ").append(evoInfo.getCurrentStage()).append(" / ").append(evoItem.getFinalStage()).append("\n");
        sb.append("Exp: ").append(evoInfo.getExperience()).append(" / ").append(evoItem.getExpForNextStage(evoInfo.getCurrentStage())).append("\n");
        sb.append("Specialty: ").append(evoInfo.getSpecialty());
        player.sendMessage(sb.toString());
    }
}
