package dice;

import java.util.Random;

public class Dice {
    private int value;
    private static final Random rand = new Random();

    public Dice() {
        roll();
    }

    public void roll() {
        this.value = rand.nextInt(6) + 1;
    }

    public int getValue() {
        return value;
    }
}
