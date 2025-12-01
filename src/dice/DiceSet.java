package dice;

public class DiceSet {

    private static final int SIZE = 5;
    private Dice[] dice;

    public DiceSet() {
        dice = new Dice[SIZE];
        for (int i = 0; i < SIZE; i++) {
            dice[i] = new Dice();
        }
    }

    public void rollAll() {
        for (Dice d : dice) {
            d.roll();
        }
    }

    public Dice[] getDice() {
        return dice;
    }
}
