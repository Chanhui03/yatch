package storage;

import java.util.List;
import java.util.Map;
import game.GameState;

public class GameSaveData {

    public int currentPlayer;
    public boolean hasRolled;
    public int[] diceValues;
    public List<PlayerData> players;

    public static class PlayerData {
        public String name;
        public Map<String, Integer> scoreBoard;
    }

    // GameState -> GameSaveData
    public static GameSaveData fromState(GameState s) {
        GameSaveData data = new GameSaveData();

        data.currentPlayer = s.getCurrentPlayer();
        data.hasRolled = s.hasRolled();

        data.diceValues = new int[5];
        for (int i = 0; i < 5; i++)
            data.diceValues[i] = s.getDiceSet().getDices()[i].getValue();

        data.players = s.toPlayerDataList();
        return data;
    }
}
