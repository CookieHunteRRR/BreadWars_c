package me.cookiehunterrr.breadwars.classes.crews;

import me.cookiehunterrr.breadwars.classes.gamesession.GameSession;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.cookiehunterrr.breadwars.BreadWars.playerInfoManager;

// Класс, отвечающий за взаимодействие между двумя командами в игровой сессии
public class SessionCrewManager
{
    GameSession session;
    final Crew crewA;
    final Crew crewB;
    int flagLimit;
    Map<Crew, CrewGameInfo> crewGameInfoMap;

    public SessionCrewManager(GameSession session, Crew crewA, Crew crewB)
    {
        this.session = session;
        this.crewA = crewA;
        this.crewB = crewB;
        this.crewA.setCurrentGameSession(session);
        this.crewB.setCurrentGameSession(session);
        this.crewGameInfoMap = new HashMap<>();
        crewGameInfoMap.put(crewA, new CrewGameInfo());
        crewGameInfoMap.put(crewB, new CrewGameInfo());
    }

    public Crew getCrewA() { return crewA; }
    public Crew getCrewB() { return crewB; }
    public CrewGameInfo getCrewGameInfo(Crew crew) { return crewGameInfoMap.get(crew); }
    public void changeFlagOwner(Crew crew, Player oldOwner, Player newOwner)
    {
        crewGameInfoMap.get(crew).changeFlagOwner(oldOwner, newOwner);
    }
    public void addCrewScore(Crew crew, int i)
    {
        crewGameInfoMap.get(crew).addScore(i);
        session.getScoreboardManager().updateScoreboardsForPlayers();
        checkScoreExcess();
    }
    public int getCrewScore(Crew crew) { return crewGameInfoMap.get(crew).score; }
    public String getCrewFlagOwnersAsString(Crew crew)
    {
        StringBuilder toOutput = new StringBuilder();
        for (Player player : getCrewFlagOwners(crew))
        {
            if (crew.crewMembers.contains(player))
                toOutput.append("⚑");
            else toOutput.append("✘");
        }
        return toOutput.toString();
    }
    public ArrayList<Player> getCrewFlagOwners(Crew crew) { return crewGameInfoMap.get(crew).flagOwners; }
    public int getScoreLimit() { return 60 * flagLimit; }
    public GameSession getSession() { return session; }
    public void killSession()
    {
        session = null;
        crewA.setCurrentGameSession(null);
        crewB.setCurrentGameSession(null);
    }

    public List<Player> getAllPlayersInSession()
    { return Stream.concat(crewA.crewMembers.stream(), crewB.crewMembers.stream()).collect(Collectors.toList()); }

    void checkScoreExcess()
    {
        if (getCrewScore(crewA) >= getScoreLimit())
            session.changeStateToEnded(crewA);
        else if (getCrewScore(crewB) >= getScoreLimit())
            session.changeStateToEnded(crewB);
    }

    /*
    Различные сообщения
    */
    public void sendSessionFormedMessage()
    {
        crewA.crewLeader.sendMessage("§aПротивник принял вызов. Ожидание запуска игры (/game start)");
        crewB.crewLeader.sendMessage("§aВы приняли вызов. Ожидание запуска игры (/game start)");
    }

    String showCrewMembersInOrder()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("§3Игроки команды ").append(crewA.crewName).append(":§r\n");
        for (Player member : crewA.crewMembers)
            sb.append(member.getDisplayName()).append(" ");
        sb.append("\n");
        sb.append("§3Игроки команды ").append(crewB.crewName).append(":§r\n");
        for (Player member : crewB.crewMembers)
            sb.append(member.getDisplayName()).append(" ");
        return sb.toString();
    }

    public void sendSessionStartedMessage()
    {
        String message = "§aОбе команды готовы к бою. Начинаем игру";
        crewA.crewLeader.sendMessage(message);
        crewB.crewLeader.sendMessage(message);
        String playersInSession = showCrewMembersInOrder();
        for (Player player : getAllPlayersInSession())
            player.sendMessage(playersInSession);
    }

    /*
    Прочие методы
    */
    public void initializeDataOnGameStart()
    {
        flagLimit = setFlagLimit();
        for (int i = 0; i < this.flagLimit; i++)
        {
            crewGameInfoMap.get(crewA).flagOwners.add(crewA.crewMembers.get(i));
            crewGameInfoMap.get(crewB).flagOwners.add(crewB.crewMembers.get(i));
        }
    }

    public Crew getOppositeCrew(Crew senderCrew)
    {
        if (senderCrew.equals(crewA))
            return crewB;
        return crewA;
    }

    public int[] getPlayerFlagInventory(Player player)
    {
        int allyFlags = 0;
        int enemyFlags = 0;
        Crew playerCrew = playerInfoManager.getPlayerInfo(player.getUniqueId()).getCrew();
        for (Player flagOwner : crewGameInfoMap.get(playerCrew).flagOwners) { if (flagOwner == player) allyFlags++; }
        if (allyFlags < 2)
        {
            Crew enemyCrew = getOppositeCrew(playerCrew);
            if (crewGameInfoMap.get(enemyCrew).flagOwners.contains(player)) enemyFlags++;
        }
        return new int[]{allyFlags, enemyFlags};
    }

    public String getPlayerFlagInventoryAsString(Player player)
    {
        // С - союзный, В - вражеский, Х - отсутствует
        // ХХ, СХ, СС, СВ, ХВ
        StringBuilder sb = new StringBuilder();
        int[] playerFlags = getPlayerFlagInventory(player);
        sb.append("§a⚑".repeat(playerFlags[0]));
        sb.append("§4⚑".repeat(playerFlags[1]));
        sb.append("§r✘".repeat(2 - (sb.length() / 3))); // делится на 3, потому что §aC это 3 символа
        return sb.toString();
    }

    public Player getPlayerAbleToCarryAllyFlag(Crew crew)
    {
        for (Player player : crew.crewMembers)
        {
            int[] playerFlags = getPlayerFlagInventory(player);
            // Если у игрока заполнен инвентарь флагов, то ищем другого
            if (playerFlags[0] + playerFlags[1] > 1) continue;
            // Если у игрока нет вражеского флага (значит свободен как минимум 1 слот для союзного флага)
            if (playerFlags[1] < 1) return player;
        }
        // Если же все пошло в жопу и ничего не сработало, выдаем ошибку в консоль потому что так быть не должно
        System.out.println("[BreadWars SCM] В методе getPlayerAbleToCarryAllyFlag() произошло возвращение null");
        System.out.println("По неизвестной причине, найти игрока способного нести союзный флаг невозможно");
        return null;
    }

    // Проверка на возможность передачи игроку указанного типа флага (вражеского или союзного)
    // Проверка должна идти от лица нападающего, то есть если враг пытается забрать у союзника флаг
    // Если нужно узнать, может ли игрок команды А нести флаг команды Б, то передаем (игрок команды А, false)
    public boolean isAbleToCarryFlagType(Player player, boolean trueIfAlly)
    {
        int[] playerFlags = getPlayerFlagInventory(player);

        if (trueIfAlly)
        {
            return playerFlags[0] + playerFlags[1] < 2;
        }
        return playerFlags[1] < 1;
    }

    /*
    Приватные методы про которые можно вроде как забыть
    */
    // Лимит флагов будет адаптироваться под кол-во игроков
    int setFlagLimit() { return Math.min(crewA.crewMembers.size(), crewB.crewMembers.size()); }
}
