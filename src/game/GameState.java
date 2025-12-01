package game;

import dice.DiceSet;
import player.Player;
import storage.GameSaveData;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    private Player[] players;
    private int currentPlayer;
    private DiceSet diceSet;
    private boolean hasRolled;

    // 새 게임
    public GameState(Player[] players) {
        this.players = players;
        this.currentPlayer = 0;
        this.diceSet = new DiceSet();
        this.hasRolled = false;
    }

    // 로드된 게임에서 복원
    public GameState(GameSaveData data) {
        this.currentPlayer = data.currentPlayer;
        this.hasRolled = data.hasRolled;

        // Player 재구성
        this.players = new Player[data.players.size()];
        for (int i = 0; i < data.players.size(); i++) {
            var pd = data.players.get(i);
            this.players[i] = new Player(pd.name, new score.ScoreBoard(pd.scoreBoard));
        }

        // DiceSet 재구성
        this.diceSet = new DiceSet();
        for (int i = 0; i < 5; i++) {
            diceSet.getDices()[i].release();
            diceSet.getDices()[i].roll();
        }
    }

    // PlayerData 변환용
    public List<GameSaveData.PlayerData> toPlayerDataList() {
        List<GameSaveData.PlayerData> list = new ArrayList<>();

        for (Player p : players) {
            GameSaveData.PlayerData pd = new GameSaveData.PlayerData();
            pd.name = p.getName();
            pd.scoreBoard = p.getScoreBoard().getAllScores();
            list.add(pd);
        }

        return list;
    }

    // Getters
    public Player[] getPlayers() { return players; }
    public int getCurrentPlayer() { return currentPlayer; }
    public DiceSet getDiceSet() { return diceSet; }
    public boolean hasRolled() { return hasRolled; }

    public void setHasRolled(boolean rolled) { hasRolled = rolled; }

    public void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % players.length;
        hasRolled = false;
    }
}
