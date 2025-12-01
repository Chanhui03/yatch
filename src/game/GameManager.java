package game;

import dice.DiceSet;
import java.util.*;
import player.Player;
import screen.Screen;
import storage.FileIOManager;

public class GameManager {

    private Player[] players;
    private int currentPlayer;
    private DiceSet diceSet;
    private Screen screen;
    private boolean hasRolled;
    private FileIOManager fileManager;


    public GameManager() {
        this.currentPlayer = 0;
        this.screen = new Screen();
        this.fileManager = new FileIOManager();
    }

    public void start() {
        screen.showWelcome();
        initPlayers();
        gameLoop();
        screen.showGameOver();
    }

    private void initPlayers() {
        // TODO: 사용자 입력 받아 Player 생성
    }

    private void gameLoop() {
        // TODO: 턴 기반 게임 루프 구현
    }

    public GameSaveData toSaveData() {
        GameSaveData data = new GameSaveData();
        data.currentPlayer = this.currentPlayer;
        data.hasRolled = this.hasRolled;

        // Dice 값 저장
        data.diceValues = new int[5];
        for (int i = 0; i < 5; i++) {
            data.diceValues[i] = diceSet.getDice()[i].getValue();
        }

        // 플레이어 정보 저장
        data.players = new ArrayList<>();
        for (Player p : players) {
            GameSaveData.PlayerData pd = new GameSaveData.PlayerData();
            pd.name = p.getName();
            pd.scoreBoard = p.getScoreBoard().getAllScores();
            data.players.add(pd);
        }
        return data;
    }

}
