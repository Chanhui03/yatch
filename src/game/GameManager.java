package game;

import screen.Screen;
import storage.SaveSystem;

public class GameManager {

    private Screen screen = new Screen();
    private SaveSystem saveSystem = new SaveSystem();
    private GameState state;

    public void start() {
        screen.showWelcome();

        if (screen.askLoadGame()) {
            state = saveSystem.load();
            if (state == null) {
                screen.show("저장된 게임 없음 → 새 게임 시작");
                state = newGame();
            }
        } else {
            state = newGame();
        }

        gameLoop();
        screen.showGameOver();
    }

    private GameState newGame() {
        return new GameState(screen.askPlayers());
    }

    private void gameLoop() {
        while (!isFinished()) {
            screen.showTurn(state);

            int cmd = screen.askCommand();
            handleCommand(cmd);
        }
    }

    private boolean isFinished() {
        return false; // TODO
    }

    private void handleCommand(int cmd) {
        switch (cmd) {
            case 1 -> handleRoll();
            case 2 -> handleHold();
            case 3 -> handleScore();
            case 9 -> saveSystem.save(state);
            default -> screen.show("잘못된 명령입니다.");
        }
    }

    private void handleRoll() {
        state.getDiceSet().reRoll();
        state.setHasRolled(true);
    }

    private void handleHold() {
        // TODO: Screen이 hold할 index를 물어보도록 구성
    }

    private void handleScore() {
        // TODO: ScoreBoard / ScoreCategory 연동
    }
}
