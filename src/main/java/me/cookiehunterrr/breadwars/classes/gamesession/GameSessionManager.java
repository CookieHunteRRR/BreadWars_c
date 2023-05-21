package me.cookiehunterrr.breadwars.classes.gamesession;

import java.util.ArrayList;

public class GameSessionManager
{
    ArrayList<GameSession> activeGameSessions;

    public GameSessionManager()
    {
        activeGameSessions = new ArrayList<>();
    }

    public void addSession(GameSession session)
    {
        activeGameSessions.add(session);
    }

    public void removeSession(GameSession session)
    {
        session.deactivateSession();
        activeGameSessions.remove(session);
    }

    public ArrayList<GameSession> getAllSessions() { return activeGameSessions; }
    public GameSession getSessionByIndex(int index) { return activeGameSessions.get(index); }
}
