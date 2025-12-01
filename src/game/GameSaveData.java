package game;

import java.util.*;

public class GameSaveData {
    public int currentPlayer;
    public boolean hasRolled;
    public List<PlayerData> players;
    public int[] diceValues;

    public static class PlayerData {
        public String name;
        public java.util.Map<String, Integer> scoreBoard;
    }
}
