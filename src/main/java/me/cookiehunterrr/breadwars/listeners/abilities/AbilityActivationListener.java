package me.cookiehunterrr.breadwars.listeners.abilities;

import me.cookiehunterrr.breadwars.classes.abilities.AbilityActivationButton;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityInstance;
import me.cookiehunterrr.breadwars.classes.abilities.IDeactivateable;
import me.cookiehunterrr.breadwars.classes.abilities.abilities.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class AbilityActivationListener implements Listener
{
    // Самый первый хендлер, все остальные хендлеры должны иметь приоритет выше, чтобы имели значение (вроде как)
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAbilityActivation(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();
        AbilityInstance abilityInstance = playerInfoManager.getPlayerInfo(player.getUniqueId()).getClassAbility();
        if (abilityInstance == null) return;
        Ability playerAbility = abilityInstance.ability.getAbility();
        if (!AbilityActivationButton.isCorrectButton(playerAbility.getAbilityActivationButton(), e.getAction())) return;
        // Конченное решение проблемы с двойным прожатием абилки / спама активацией абилки
        // По какой-то причине, например, у часов невидимости при наведении на блок дважды активируется этот ивент
        // Тем самым в чат вылезает спам "Вы прервали активацию абилки"
        if ((!playerAbility.hasBlockInteractionRequirement()) &&
                (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        // Сначала проверяем, требует ли абилка конкретный предмет (не материал абилки) в руках для активации
        if (playerAbility.hasItemRequirement())
        {
            // Затем проверяем является ли предмет в руках необходимым предметом для активации
            if (!(playerAbility.getRequiredItemMaterial() == mainHandItem.getType())) return;
            // Затем проверяем есть ли необходимость в определенном названии предмета,
            // То есть условно подойдет любой лук или нужен лук с конкретным названием
            if (playerAbility.getRequiredItemName() != null)
            {
                try
                {
                    // Непосредственно проверка соответствует ли название предмета в руках требуемому названию для активации
                    if (!(playerAbility.getRequiredItemName().equals(mainHandItem.getItemMeta().getDisplayName())))
                        return;
                }
                catch (Exception ex) { return; }
            }
        }
        // Если же не требует конкретного предмета, проверяет, держит ли игрок предмет-абилку
        else
        {
            try
            {
                if (!mainHandItem.getItemMeta().getDisplayName().equals(abilityInstance.ability.getAbilityName()))
                    return;
            }
            catch (NullPointerException ex) { return; }
        }
        // Запрещаем как-то использовать абилити айтемы
        //e.setCancelled(true);
        // Проверяем является ли абилка деактивируемой и активирована ли она
        if (playerAbility instanceof IDeactivateable)
        {
            // Будет значить, что абилка запущена
            if (abilityInstance.info.taskID != 0)
            {
                ((IDeactivateable) playerAbility).deactivate(player);
                return;
            }
        }
        // Затем уже проверяет не на кулдауне ли абилка
        long timeSinceAbilityUse = (System.currentTimeMillis() - abilityInstance.info.lastActivation) / 1000;
        if (timeSinceAbilityUse < playerAbility.getCooldown())
        {
            player.sendMessage("Кулдаун: " + (playerAbility.getCooldown() - timeSinceAbilityUse));
            return;
        }
        // Непосредственно активация абилки
        playerAbility.activate(player);
    }
}
