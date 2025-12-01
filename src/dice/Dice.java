package dice;

import java.util.Random;

public class Dice {
    private int value;
    private static final Random rand = new Random();
    private boolean isHeld;

    public Dice() {
        this.value = 0;
        this.isHeld = false;
    }

    public void setFromSavedData(int value, boolean isHeld) {
        this.value = value;
        this.isHeld = isHeld;
    }

    public void roll() {
        if (isHeld) return;
        this.value = rand.nextInt(6) + 1;
    }

    public void hold() {
        this.isHeld = true;
    }

    public void release() {
        this.isHeld = false;
    }

    public boolean isHeld() {
        return isHeld;
    }

    public int getValue() {
        return value;
    }
}
