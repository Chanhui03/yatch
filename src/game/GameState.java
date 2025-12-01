package game;

import dice.DiceSet;
import player.Player;

public class GameState {

    private final Player[] players;
    private final DiceSet diceSet;
    private int currentPlayerIndex;
    private int rerollsLeft;

    public GameState(Player[] players) {
        this.players = players;
        this.diceSet = new DiceSet(5);
        this.currentPlayerIndex = 0;
        this.rerollsLeft = 3;
    }

    public Player[] getPlayers() {
        return players;
    }

    public DiceSet getDiceSet() {
        return diceSet;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void advanceToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }

    public int getRerollsLeft() {
        return rerollsLeft;
    }

    public void setRerollsLeft(int rerollsLeft) {
        this.rerollsLeft = rerollsLeft;
    }

    public void decrementRerolls() {
        if (rerollsLeft > 0) rerollsLeft--;
    }
}
