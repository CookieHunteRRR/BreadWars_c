package me.cookiehunterrr.breadwars.classes.customitems;

import me.cookiehunterrr.breadwars.BreadWars;
import org.bukkit.NamespacedKey;

public enum CustomAttribute
{
    // null - нет фиксированного слота
    // какой-либо integer - номер фиксированного слота
    FIXED_SLOT("fixedslot"),
    // null - нет соулбаунда
    // 0 - плеербаунд (предмет не может покинуть инвентарь никаким способом)
    // 1 - обычный соулбаунд (при смерти предмет остается в инвентаре и ничего более)
    // 2+ - временный соулбаунд (пока не ввожу)
    SOULBOUND("soulbound"),
    EVO("evo");

    String key;

    CustomAttribute(String keyName)
    {
        this.key = keyName;
    }

    public String getKey() { return this.key; }
    public NamespacedKey getAsNamespacedKey() { return new NamespacedKey(BreadWars.getInstance(), this.key); }
}
