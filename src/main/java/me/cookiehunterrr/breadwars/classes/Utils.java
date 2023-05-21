package me.cookiehunterrr.breadwars.classes;

import me.cookiehunterrr.breadwars.BreadWars;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;

public class Utils
{
    static long getSecondsSinceGameStarted(long whenGameStarted)
    {
        return (System.currentTimeMillis() / 1000 - whenGameStarted / 1000);
    }

    public static String getTimeSinceValueAsString(long value)
    {
        long seconds = getSecondsSinceGameStarted(value);
        long minutes = seconds / 60;
        return String.format("%dm %ds", minutes, (seconds - 60*minutes));
    }

    public static void changePlayerHealth(Player target, String changeType, double amount)
    {
        double currentHP = target.getHealth();
        switch (changeType)
        {
            case "heal":
                if (currentHP + amount >= target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
                    target.setHealth(20);
                else target.setHealth(currentHP + amount);
                break;
            case "damage":
                if (currentHP - amount <= 0)
                    target.setHealth(0);
                else target.setHealth(currentHP - amount);
                break;
            case "set":
                target.setHealth(amount);
                break;
        }
    }

    public static ArrayList<Player> getNearbyPlayers(Player origin, double radius)
    {
        ArrayList<Player> nearby = new ArrayList<>();
        for (Entity e : origin.getNearbyEntities(radius, radius, radius))
        {
            if (e instanceof Player player) nearby.add(player);
        }
        return nearby;
    }

    public static void sendMessageToActionBar(Player player, String message)
    {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public static Enchantment getEnchantmentByName(String name)
    {
        return EnchantmentWrapper.getByKey(NamespacedKey.minecraft(name));
    }

    public static boolean checkProbability(double chance)
    {
        return BreadWars.getRandom().nextDouble() <= chance;
    }
    public static int getRandomIntegerInRange(int min, int max) { return BreadWars.getRandom().nextInt(max-min)+min; }

    public static double getDistanceBetweenLocations(Location first, Location second)
    {
        // Вычитаем короче длину вектора одной локации из другой локации
        return first.toVector().subtract(second.toVector()).length();
    }

    // Не передавать в этот метод ничего, кроме брони
    public static int getAppropriateSlotIndex(EquipmentSlot equipmentSlot)
    {
        switch (equipmentSlot)
        {
            case HEAD -> { return 39; }
            case CHEST -> { return 38; }
            case LEGS -> { return 37; }
            case FEET -> { return 36; }
            default -> { return 0; }
        }
    }
}