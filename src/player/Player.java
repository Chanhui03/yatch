package player;

import score.ScoreBoard;

public class Player {

    private String name;
    private ScoreBoard scoreBoard;

    // 새 플레이어 생성
    public Player(String name) {
        this.name = name;
        this.scoreBoard = new ScoreBoard();
    }

    // 저장 로드 전용 생성자
    public Player(String name, ScoreBoard board) {
        this.name = name;
        this.scoreBoard = board;
    }

    public String getName() { return name; }
    public ScoreBoard getScoreBoard() { return scoreBoard; }
}
