package me.cookiehunterrr.breadwars.classes.crews;

import me.cookiehunterrr.breadwars.classes.Job;
import me.cookiehunterrr.breadwars.classes.gamesession.GameSession;
import me.cookiehunterrr.breadwars.classes.playerinfo.PlayerInfo;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

public class Crew
{
    String crewName;
    Player crewLeader;
    ArrayList<Player> crewMembers;
    GameSession currentGameSession;

    public Crew(String name, Player leader)
    {
        this.crewMembers = new ArrayList<>();
        this.crewName = name;
        this.crewLeader = leader;
        this.currentGameSession = null;
        addPlayer(leader);
    }

    public void sendMessageToCrew(Player whoSent, String message)
    {
        for (Player player : crewMembers)
            player.sendMessage("§a<[Crew] " + whoSent.getDisplayName() + "> §r" + message);
    }

    public boolean addPlayer(Player player)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        if (crewMembers.size() < 4 && playerInfo.getCrew() == null) {
            for (Player crewmate : crewMembers)
                crewmate.sendMessage("§a" + player.getDisplayName() + " §3вступил в вашу команду");
            crewMembers.add(player);
            playerInfo.setCrew(this);
            return true;
        }
        return false;
    }

    public void removePlayer(Player player)
    {
        PlayerInfo playerInfo = playerInfoManager.getPlayerInfo(player.getUniqueId());
        playerInfo.resetPlayerInfo();
        crewMembers.remove(player);
    }

    public void removePlayerWithNotification(Player player)
    {
        removePlayer(player);
        for (Player crewmate : crewMembers)
            crewmate.sendMessage("§a" + player.getDisplayName() + " §3покинул вашу команду");
    }

    public void reconnectPlayer(Player player)
    {
        UUID playerID = player.getUniqueId();
        for (Player crewmate : crewMembers)
        {
            if (crewmate.getUniqueId() == playerID)
            {
                crewMembers.remove(crewmate);
                crewMembers.add(player);
                break;
            }
        }
        for (Player crewmate : crewMembers)
            crewmate.sendMessage("§a" + player.getDisplayName() + " §3перезашел в игру");
    }

    public void changeLeader(Player curLeader, Player newLeader)
    {
        if (curLeader == crewLeader && crewMembers.contains(newLeader))
        {
            crewLeader = newLeader;
            newLeader.sendMessage("§aТеперь вы являетесь лидером команды " + crewName);
        }
    }

    public boolean isJobTaken(Job job)
    {
        for (Player crewMember : crewMembers)
        {
            if (playerInfoManager.getPlayerInfo(crewMember.getUniqueId()).getJob() == job)
                return true;
        }
        return false;
    }

    // Проверка для запуска игры. Все игроки в команде должны иметь класс
    public boolean doEveryPlayerHaveJob()
    {
        for (Player player : crewMembers)
            if (playerInfoManager.getPlayerInfo(player.getUniqueId()).getJob() == null) return false;
        return true;
    }

    //public void setCrewName(String crewName) { this.crewName = crewName; }
    public void setCurrentGameSession(GameSession session) { this.currentGameSession = session; }
    public void setCrewLeader(Player player) { this.crewLeader = player; }
    public String getCrewName() { return crewName; }
    public Player getCrewLeader() { return crewLeader; }
    public ArrayList<Player> getCrewMembers() { return crewMembers; }
    public GameSession getCurrentGameSession() { return currentGameSession; }
}
