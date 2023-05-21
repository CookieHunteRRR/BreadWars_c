package me.cookiehunterrr.breadwars.classes.crews;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CrewGameInfo
{
    ArrayList<Player> flagOwners;
    int score;
    Location respawnLocation;

    public CrewGameInfo()
    {
        flagOwners = new ArrayList<>();
        score = 0;
    }

    public void changeFlagOwner(Player oldOwner, Player newOwner)
    {
        if (flagOwners.remove(oldOwner))
            flagOwners.add(newOwner);
        else System.out.println("[BreadWars CrewGameInfo] В changeFlagOwner() был отправлен несуществующий oldOwner");
    }

    public void setRespawnLocation(Location location) { this.respawnLocation = location; }
    public Location getRespawnLocation() { return respawnLocation; }
    //public void setScore(int score) { this.score = score; }
    public void addScore(int score) { this.score += score; }
}
