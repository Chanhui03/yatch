package player;

public class Player {

    private final String name;
    private final ScoreBoard scoreBoard;
    //플레이어 이름과 점수판 초기화
    public Player(String name) {
        this.name = name;
        this.scoreBoard = new ScoreBoard();
    }
    //플레이어 이름 반환
    public String getName() {
        return name;
    }
    //점수판 객체 반환
    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }
}
