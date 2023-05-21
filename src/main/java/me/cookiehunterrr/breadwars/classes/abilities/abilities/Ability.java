package me.cookiehunterrr.breadwars.classes.abilities.abilities;

import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityActivationButton;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class Ability
{
    // Каждая абилка будет иметь хардкодед баттон, абилити тайп, job и кд будут браться из конфига
    AbilityType abilityType;
    AbilityActivationButton abilityActivationButton;
    Job requiredJob;
    // Если requiredItem = null, то будет проверяться нажатие кнопки с абилкой в руках. Если же что-то другое
    // Например, лук с Freezing Arrow, тогда сначала проверяется используется ли нужный предмет, а если нужно еще и предмет с конкретным названием
    // тогда проверяется и оно. Короче все нули приводят к скипу каких то проверок.
    RequiredMainHandItem requiredItem;
    boolean requiresBlockInteraction;
    int cooldown;

    public abstract void activationMessage(Player player);
    public abstract void deactivationMessage(Player player);
    public abstract void activate(Player player);

    public AbilityType getAbilityType() { return abilityType; }
    public Job getRequiredJob() { return requiredJob; }
    public AbilityActivationButton getAbilityActivationButton() { return abilityActivationButton; }
    public int getCooldown() { return cooldown; }
    public boolean hasItemRequirement() { return !(requiredItem == null); }
    public boolean hasBlockInteractionRequirement() { return requiresBlockInteraction; }
    public Material getRequiredItemMaterial() { return requiredItem.itemMaterial; }
    public String getRequiredItemName() { return requiredItem.itemName; }

    protected class RequiredMainHandItem
    {
        Material itemMaterial;
        String itemName;

        RequiredMainHandItem(@NonNull Material itemMaterial, String itemName)
        {
            this.itemMaterial = itemMaterial;
            this.itemName = itemName;
        }
    }
}