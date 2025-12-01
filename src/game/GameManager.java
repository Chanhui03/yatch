package game;

import dice.DiceSet;
import java.util.List;
import player.Player;

public class GameManager {

    private GameState state;

    // 새 게임 시작
    public void startNewGame(int playerCount) {
        Player[] players = new Player[playerCount];
        for (int i = 0; i < playerCount; i++) {
            players[i] = new Player("Player " + (i + 1));
        }
        state = new GameState(players);
        startTurn();   // ▶️ 첫 턴 시작 (주사위는 전부 ? 상태)
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
     * 🔁 턴 시작
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
     * 🎲 Roll 버튼 눌렀을 때
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
        return ScoreCalculator.calculate(category, values);
    }

    /**
     * ✅ 점수 기록
     */
    public int applyScore(ScoreCategory category) {
        int score = previewScore(category);
        getCurrentPlayer().getScoreBoard().recordScore(category, score);

        // 게임이 아직 안 끝났으면 다음 플레이어로
        if (!isGameFinished()) {
            state.advanceToNextPlayer();
            startTurn();  // ▶️ 다음 플레이어 턴 시작 (다시 ? + 3회)
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
}
