package me.cookiehunterrr.breadwars.listeners.items;

import me.cookiehunterrr.breadwars.BreadWars;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import me.cookiehunterrr.breadwars.classes.customitems.CustomAttribute;
import me.cookiehunterrr.breadwars.classes.customitems.CustomItemManager;
import me.cookiehunterrr.breadwars.classes.customitems.evo.EVOItem;
import me.cookiehunterrr.breadwars.classes.customitems.evo.EvoDataType;
import me.cookiehunterrr.breadwars.classes.customitems.evo.EvoItemInformation;
import me.cookiehunterrr.breadwars.classes.gamesession.SessionState;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class EVOHandler implements Listener
{
    final private ArrayList<EntityDamageEvent.DamageCause> appropriateKillCause;
    final private ArrayList<EntityDamageEvent.DamageCause> appropriateDamageCause;

    public EVOHandler()
    {
        appropriateKillCause = new ArrayList<>();
        appropriateKillCause.add(EntityDamageEvent.DamageCause.PROJECTILE);
        appropriateKillCause.add(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        appropriateKillCause.add(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);

        appropriateDamageCause = new ArrayList<>();
        appropriateDamageCause.add(EntityDamageEvent.DamageCause.PROJECTILE);
        appropriateDamageCause.add(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        appropriateDamageCause.add(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
        appropriateDamageCause.add(EntityDamageEvent.DamageCause.FALL);
    }

    // Метод на вход получает вскопанный блок и инструмент, которым блок вскопали
    // Возвращает либо соответствующую тирам руды экспу, либо экспу за подходящий блок, либо 0
    double getAppropriateExpForPickaxe(Block block)
    {
        Material material = block.getType();
        String expToGet;
        switch (material)
        {
            case COAL_ORE:
            case IRON_ORE:
            case COPPER_ORE:
                expToGet = "t1_ore_mined";
                break;
            case REDSTONE_ORE:
            case GOLD_ORE:
            case LAPIS_ORE:
            case NETHER_QUARTZ_ORE:
            case NETHER_GOLD_ORE:
                expToGet = "t2_ore_mined";
                break;
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case ANCIENT_DEBRIS:
                expToGet = "t3_ore_mined";
                break;
            default:
                expToGet = "appropriate_block_mined";
                break;
        }
        return BreadWars.getInstance().getConfig().getDouble(String.format("evo-experience.tools.pickaxe.%s", expToGet));
    }

    double getAppropriateExpForAxe(Block block)
    {
        Material material = block.getType();
        String expToGet;
        switch (material)
        {
            case ACACIA_LOG:
            case BIRCH_LOG:
            case DARK_OAK_LOG:
            case OAK_LOG:
            case JUNGLE_LOG:
            case SPRUCE_LOG:
            case WARPED_STEM:
            case CRIMSON_STEM:
                expToGet = "log_mined";
                break;
            default:
                expToGet = "appropriate_block_mined";
                break;
        }
        return BreadWars.getInstance().getConfig().getDouble(String.format("evo-experience.tools.axe.%s", expToGet));
    }

    // Универсальный метод для добавления эво экспы с проверками на переход на другую стадию
    void addEvoExperience(ItemStack item, EVOItem evoItem, EvoItemInformation evoInfo, double exp)
    {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (evoInfo.getExperience() + exp < evoItem.getExpForNextStage(evoInfo.getCurrentStage()))
        {
            evoInfo.addExperience(exp);
            dataContainer.set(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType(), evoInfo);
            item.setItemMeta(meta);
        }
        else
        {
            evoInfo.addExperience(exp);
            evoInfo.setCurrentStage(evoInfo.getCurrentStage() + 1);
            dataContainer.set(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType(), evoInfo);
            item.setType(evoItem.getStageMaterial(evoInfo.getCurrentStage()));
            item.setItemMeta(meta);
        }
        CustomItemManager.setUpdatedItemMeta(item);
    }

    // Получение экспы у оружия
    @EventHandler
    public void handleEVOKill(EntityDeathEvent e)
    {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        PlayerInfo killerInfo = playerInfoManager.getPlayerInfo(killer.getUniqueId());
        EntityDamageEvent.DamageCause damageCause;

        try { damageCause = e.getEntity().getLastDamageCause().getCause(); }
        catch (NullPointerException ex) { return; }
        // Если причина смерти не из тех, что могут добавить экспу для меча/лука
        if (!appropriateKillCause.contains(damageCause)) return;

        try
        {
            if (killerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        ItemStack mainHandItem;
        ItemMeta meta;
        PersistentDataContainer dataContainer;
        try {
            mainHandItem = killer.getInventory().getItemInMainHand();
            meta = mainHandItem.getItemMeta();
            dataContainer = meta.getPersistentDataContainer();
        } catch (NullPointerException ex) { return; }

        // Если не ЭВО предмет
        if (!dataContainer.has(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType())) return;
        EvoItemInformation evoInfo = dataContainer.get(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType());
        EVOItem evoItem = EVOItem.getByOrdinal(evoInfo.getEvoID());
        if (evoItem.isFinalStage(evoInfo.getCurrentStage())) return;
        // Если не меч/топор/лук
        if (!(evoItem == EVOItem.EVO_SWORD || evoItem == EVOItem.EVO_AXE || evoItem == EVOItem.EVO_BOW)) return;

        // Получается у нас гарантировано в руках ЭВО оружие, теперь ищем нужную экспу из конфига
        String expToGet;
        if (e.getEntity() instanceof Player victim)
        {
            // Сначала проверяем есть ли у него флаг команды killer
            Crew killerCrew = killerInfo.getCrew();
            if (killerCrew.getCurrentGameSession().getCrewManager().getCrewFlagOwners(killerCrew).contains(victim))
                expToGet = "flag_theft_kill";
            else expToGet = "player_kill";
        }
        else if (e.getEntity() instanceof Monster)
            expToGet = "hostile_mob_kill";
        else expToGet = "passive_mob_kill";

        // Как это ужасно
        double expToAdd = BreadWars.getInstance().getConfig().getDouble(String.format("evo-experience.weapons.%s.%s",
                evoItem.getItemTypeAsString(), expToGet));

        // Напоследок проверяем, поменялась ли стадия, если поменялась - обновляем материал
        addEvoExperience(mainHandItem, evoItem, evoInfo, expToAdd);
    }

    // Получение экспы у инструментов
    @EventHandler
    public void handleEVOExcavate(BlockBreakEvent e)
    {
        Player player = e.getPlayer();
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());

        ItemStack mainHandItem;
        ItemMeta meta;
        PersistentDataContainer dataContainer;
        try {
            mainHandItem = player.getInventory().getItemInMainHand();
            meta = mainHandItem.getItemMeta();
            dataContainer = meta.getPersistentDataContainer();
        } catch (NullPointerException ex) { return; }

        Block block = e.getBlock();
        if (!(block.isPreferredTool(mainHandItem))) return;

        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        // Если не ЭВО предмет
        if (!dataContainer.has(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType())) return;
        EvoItemInformation evoInfo = dataContainer.get(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType());
        EVOItem evoItem = EVOItem.getByOrdinal(evoInfo.getEvoID());
        if (evoItem.isFinalStage(evoInfo.getCurrentStage())) return;
        // Если не топор/кирка
        if (!(evoItem == EVOItem.EVO_AXE || evoItem == EVOItem.EVO_PICKAXE)) return;

        double expToAdd;
        if (evoItem == EVOItem.EVO_PICKAXE)
            expToAdd = getAppropriateExpForPickaxe(block);
        else expToAdd = getAppropriateExpForAxe(block);

        if (!e.isCancelled())
            addEvoExperience(mainHandItem, evoItem, evoInfo, expToAdd);
    }

    // Получение экспы у брони
    @EventHandler
    public void handleEVOPlayerDamageTaken(EntityDamageByEntityEvent e)
    {
        if (!(e.getEntity() instanceof Player player)) return;
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        EntityDamageEvent.DamageCause damageCause = e.getCause();

        if (!(appropriateDamageCause.contains(damageCause))) return;
        if (!(damageCause == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                damageCause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK ||
                damageCause == EntityDamageEvent.DamageCause.PROJECTILE)) return;

        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        ArrayList<ItemStack> equippedEvoArmor = new ArrayList<>();
        for (ItemStack armor : player.getInventory().getArmorContents())
        {
            if (armor == null) continue;
            ItemMeta meta = armor.getItemMeta();
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            if (!(dataContainer.has(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType()))) continue;
            equippedEvoArmor.add(armor);
        }
        if (equippedEvoArmor.size() < 1) return;

        String expToGet;
        if (e.getDamager() instanceof Player)
        {
            if (damageCause == EntityDamageEvent.DamageCause.PROJECTILE)
                expToGet = "player_arrow_attack";
            else expToGet = "player_attack";
        }
        else expToGet = damageCause.name().toLowerCase();
        double expToAdd = BreadWars.getInstance().getConfig().getDouble(String.format("evo-experience.armor.%s", expToGet));

        if (!e.isCancelled())
        {
            for (ItemStack armor : equippedEvoArmor)
            {
                ItemMeta meta = armor.getItemMeta();
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                EvoItemInformation evoInfo = dataContainer.get(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType());
                EVOItem evoItem = EVOItem.getByOrdinal(evoInfo.getEvoID());
                if (evoItem.isFinalStage(evoInfo.getCurrentStage())) continue;
                addEvoExperience(armor, evoItem, evoInfo, expToAdd);
            }
        }
    }

    @EventHandler
    public void handleEVODamageTaken(EntityDamageEvent e)
    {
        if (!(e.getEntity() instanceof Player player)) return;
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        EntityDamageEvent.DamageCause damageCause = e.getCause();

        if (!(appropriateDamageCause.contains(damageCause))) return;
        // За эти причины урона отвечает другой листенер
        if (damageCause == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
            damageCause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK ||
            damageCause == EntityDamageEvent.DamageCause.PROJECTILE) return;

        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        ArrayList<ItemStack> equippedEvoArmor = new ArrayList<>();
        for (ItemStack armor : player.getInventory().getArmorContents())
        {
            if (armor == null) continue;
            ItemMeta meta = armor.getItemMeta();
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            if (!(dataContainer.has(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType()))) continue;
            equippedEvoArmor.add(armor);
        }
        if (equippedEvoArmor.size() < 1) return;

        String expToGet;
        if (!appropriateDamageCause.contains(damageCause)) expToGet = "other";
        else expToGet = damageCause.name().toLowerCase();
        double expToAdd = BreadWars.getInstance().getConfig().getDouble(String.format("evo-experience.armor.%s", expToGet));

        if (!e.isCancelled())
        {
            for (ItemStack armor : equippedEvoArmor)
            {
                ItemMeta meta = armor.getItemMeta();
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                EvoItemInformation evoInfo = dataContainer.get(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType());
                EVOItem evoItem = EVOItem.getByOrdinal(evoInfo.getEvoID());
                if (evoItem.isFinalStage(evoInfo.getCurrentStage())) continue;
                addEvoExperience(armor, evoItem, evoInfo, expToAdd);
            }
        }
    }

    // Получение экспы у лука
    @EventHandler
    public void handleEVOProjectileHit(ProjectileHitEvent e)
    {
        if (!(e.getHitEntity() instanceof Player victim)) return;
        if (!(e.getEntity().getShooter() instanceof Player player)) return;
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());

        ItemStack mainHandItem;
        ItemMeta meta;
        PersistentDataContainer dataContainer;
        try {
            mainHandItem = player.getInventory().getItemInMainHand();
            meta = mainHandItem.getItemMeta();
            dataContainer = meta.getPersistentDataContainer();
        } catch (NullPointerException ex) { return; }

        try
        {
            if (playerInfo.getCrew().getCurrentGameSession().getSessionState() != SessionState.GAME_ACTIVE) return;
        }
        catch (Exception ex) { return; }

        // Если не ЭВО предмет
        if (!dataContainer.has(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType())) return;
        EvoItemInformation evoInfo = dataContainer.get(CustomAttribute.EVO.getAsNamespacedKey(), new EvoDataType());
        EVOItem evoItem = EVOItem.getByOrdinal(evoInfo.getEvoID());
        if (evoItem.isFinalStage(evoInfo.getCurrentStage())) return;
        // Если не лук
        if (!(evoItem == EVOItem.EVO_BOW)) return;

        double expToAdd = BreadWars.getInstance().getConfig().getDouble("evo-experience.weapons.bow.player_hit");

        if (!e.isCancelled())
            addEvoExperience(mainHandItem, evoItem, evoInfo, expToAdd);
    }
}