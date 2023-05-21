package me.cookiehunterrr.breadwars.classes.customitems;

import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CustomItem
{
    String name;
    Material material;
    // аттрибут и соответствующее ему значение
    Map<CustomAttribute, Integer> attributesMap;

    // null на месте атрибута будет означать что атрибут не добавляем
    public CustomItem(String name, Material material, @Nullable Integer fixedSlot, @Nullable Integer soulbound)
    {
        this.name = name;
        this.material = material;
        this.attributesMap = new HashMap<>();
        // Без строгих проверок потому что не знаю как их делать
        // Надеюсь не забуду что фиксированный слот должен быть валидным слотом инвентаря
        if (fixedSlot != null)
        {
            attributesMap.put(CustomAttribute.FIXED_SLOT, fixedSlot);
        }
        if (soulbound != null)
        {
            attributesMap.put(CustomAttribute.SOULBOUND, soulbound);
        }
    }
}
