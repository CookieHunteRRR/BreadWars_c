package me.cookiehunterrr.breadwars.classes.abilities;

import org.bukkit.event.block.Action;

public enum AbilityActivationButton
{
    LEFT_CLICK,
    RIGHT_CLICK;

    public static boolean isCorrectButton(AbilityActivationButton requiredButton, Action action)
    {
        switch (requiredButton)
        {
            case LEFT_CLICK -> { return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK; }
            case RIGHT_CLICK -> { return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK; }
        }
        return false;
    }
}
