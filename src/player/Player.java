package player;

import score.ScoreBoard;

public class Player {
    private String name;
    private ScoreBoard board;

    public Player(String name) {
        this.name = name;
        this.board = new ScoreBoard();
    }

    public String getName() {
        return name;
    }

    public ScoreBoard getScoreBoard() {
        return board;
    }
}
