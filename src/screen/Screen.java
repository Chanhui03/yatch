package screen;

import game.GameState;

import player.Player;

import java.util.Scanner;

public class Screen {

    private Scanner scanner = new Scanner(System.in);

    public void showWelcome() {
        System.out.println("🎲 Welcome to Yatch Game!");
    }

    public void showGameOver() {
        System.out.println("🏁 Game Over!");
    }

    public boolean askLoadGame() {
        System.out.println("이전 게임을 불러올까요? (y/n)");
        return scanner.nextLine().trim().equalsIgnoreCase("y");
    }

    public Player[] askPlayers() {
        System.out.print("플레이어 수 입력: ");
        int n = Integer.parseInt(scanner.nextLine());

        Player[] players = new Player[n];
        for (int i = 0; i < n; i++) {
            System.out.print("플레이어 " + (i + 1) + " 이름: ");
            String name = scanner.nextLine();
            players[i] = new Player(name);
        }
        return players;
    }

    public void showTurn(GameState state) {
        Player current = state.getPlayers()[state.getCurrentPlayer()];
        System.out.println("\n===== " + current.getName() + "님의 턴 =====");

        System.out.println("주사위:");
        state.getDiceSet().getDices();
        for (var d : state.getDiceSet().getDices()) {
            System.out.print(d.getValue() + (d.isHeld() ? "(H)" : "") + " ");
        }
        System.out.println();
    }

    public int askCommand() {
        System.out.println("[1] 굴리기   [2] 홀드   [3] 점수 선택   [9] 저장");
        System.out.print("> ");
        return Integer.parseInt(scanner.nextLine());
    }
    
    public void show(String msg) {
        System.out.println(msg);
    }
}
