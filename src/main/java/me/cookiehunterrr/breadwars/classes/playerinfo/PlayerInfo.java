package me.cookiehunterrr.breadwars.classes.playerinfo;

import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.abilities.AbilityInstance;
import me.cookiehunterrr.breadwars.classes.chat.ChatChannel;
import me.cookiehunterrr.breadwars.classes.crews.Crew;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerInfo
{
    Crew crew;
    Job job;
    AbilityInstance classAbility;
    ArrayList<ItemStack> savedItemsOnDeath;
    ChatChannel chatChannel;
    //public AbilityItem jobAbilityItem;
    //public long lastJobAbilityUsage;
    //public short abilityTimer;
    //public boolean isAbilityActive;
    //public int cancellableAbilityID;
    //public ArrayList<ItemStack> savedItemsOnDeath;

    public PlayerInfo()
    {
        resetPlayerInfo();
    }

    public void resetPlayerInfo()
    {
        crew = null;
        job = null;
        classAbility = null;
        savedItemsOnDeath = null;
        chatChannel = ChatChannel.DEFAULT;
    }

    public Crew getCrew() { return crew; }
    public void setCrew(Crew crew) { this.crew = crew; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public AbilityInstance getClassAbility() { return classAbility; }
    public void setClassAbility(AbilityInstance classAbility) { this.classAbility = classAbility; }
    public void setSavedItemsOnDeath(ArrayList<ItemStack> itemsToRetain) { this.savedItemsOnDeath = itemsToRetain; }
    public ArrayList<ItemStack> retrieveAndClearSavedItems()
    {
        ArrayList<ItemStack> toReturn = new ArrayList<>(savedItemsOnDeath);
        savedItemsOnDeath.clear();
        return toReturn;
    }
    public void setChatChannel(ChatChannel channel) { this.chatChannel = channel; }
    public ChatChannel getChatChannel() { return chatChannel; }
}
