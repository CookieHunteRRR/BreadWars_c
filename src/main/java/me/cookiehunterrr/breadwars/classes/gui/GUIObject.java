package me.cookiehunterrr.breadwars.classes.gui;

import org.bukkit.inventory.ItemStack;

// Минимальная единица интерфейса. Может быть кнопкой, которая по нажатию, что-то делает, может быть просто плейсхолдером
// для информации
public class GUIObject
{
    // задать, либо null, если расположение объекта не имеет значение
    int specificSlot;

    ItemStack objectItemStack;
}
