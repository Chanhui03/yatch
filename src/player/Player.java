package player;

public class Player {

    private final String name;
    private final ScoreBoard scoreBoard;

    public Player(String name) {
        this.name = name;
        this.scoreBoard = new ScoreBoard();
    }

    public String getName() {
        return name;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }
}
