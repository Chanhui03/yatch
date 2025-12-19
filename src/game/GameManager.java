package game;

import dice.DiceSet;
import java.util.List;
import player.Player;
import save.SaveSystem;

public class GameManager {

    private GameState state;

    private final SaveSystem saveSystem = new SaveSystem();

    // 새 게임 시작
    public void startNewGame(int playerCount) {
        Player[] players = new Player[playerCount];
        for (int i = 0; i < playerCount; i++) {
            players[i] = new Player("Player " + (i + 1));
        }
        state = new GameState(players);
        startTurn();   // ▶첫 턴 시작 (주사위는 전부 ? 상태)
    }

    public GameState getState() {
        return state;
    }

    public int getPlayerCount() {
        return state.getPlayers().length;
    }

    public Player getPlayer(int index) {
        return state.getPlayers()[index];
    }

    public Player getCurrentPlayer() {
        return state.getPlayers()[state.getCurrentPlayerIndex()];
    }

    /**
     * 턴 시작
     * - 주사위 값 0으로 초기화 (UI에서 '?'로 표시)
     * - hold 해제
     * - 재굴림 횟수 3회로 리셋
     */
    public void startTurn() {
        DiceSet diceSet = state.getDiceSet();

        // 새 턴용 초기화 (아래에서 추가할 DiceSet.resetAll() 호출)
        diceSet.resetAll();

        // 이 턴에 굴릴 수 있는 횟수: 3회
        state.setRerollsLeft(3);
    }

    /**
     * Roll 버튼 눌렀을 때
     */
    public void rollDice() {
        if (state.getRerollsLeft() <= 0) return;

        state.getDiceSet().rollAll(); // 실제 굴리기
        state.decrementRerolls();     // 남은 횟수 차감
    }

    public void toggleHold(int diceIndex) {
        state.getDiceSet().toggleHold(diceIndex);
    }

    public boolean canUseCategory(ScoreCategory category) {
        return !getCurrentPlayer().getScoreBoard().isUsed(category);
    }

    public int previewScore(ScoreCategory category) {
        List<Integer> values = state.getDiceSet().getValues();
        return category.calculate(values);
    }

    /**
     * 점수 기록
     */
    public int applyScore(ScoreCategory category) {
        int score = previewScore(category);
        getCurrentPlayer().getScoreBoard().recordScore(category, score);

        // 게임이 아직 안 끝났으면 다음 플레이어로
        if (!isGameFinished()) {
            state.advanceToNextPlayer();
            startTurn();  // 다음 플레이어 턴 시작 (다시 ? + 3회)
        }
        return score;
    }

    public boolean isGameFinished() {
        for (Player p : state.getPlayers()) {
            if (!p.getScoreBoard().isFull()) {
                return false;
            }
        }
        return true;
    }

    /** 현재 게임 상태 저장 */
    public void saveGame() {
        if (state != null) {
            saveSystem.save(state);
        }
    }


    /**
     * 플레이어 수 체크 없이 그냥 세이브를 불러온다.
     * - 앱 처음 켰을 때 "불러오기"로 시작하는 용도
     */
    public boolean loadGame() {
        GameState loaded = saveSystem.load();
        if (loaded != null) {
            this.state = loaded;
            return true;
        }
        return false;
    }

    /**
     * 현재 게임의 플레이어 수와 같은 세이브만 불러온다.
     * - 이미 3인 게임을 진행 중일 때, 2인용 세이브를 실수로 불러오는 것을 막고 싶을 때 사용
     * - 현재 state가 없는 경우(= 새 게임 전)에는 그냥 loadGame()과 동일하게 동작하도록 할 수도 있음
     */
    public boolean loadGameWithSamePlayerCountOnly() {
        GameState loaded = saveSystem.load();
        if (loaded == null) return false;

        // 현재 게임이 이미 존재한다면 플레이어 수 비교
        if (this.state != null) {
            int currentCount = this.state.getPlayers().length;
            int loadedCount  = loaded.getPlayers().length;
            if (currentCount != loadedCount) {
                // 인원 수 다르면 불러오기 거절
                return false;
            }
        }

        this.state = loaded;
        return true;
    }
    
    /** 세이브 파일 존재 여부 */
    public boolean hasSave() {
        return saveSystem.hasSave();
    }
}
