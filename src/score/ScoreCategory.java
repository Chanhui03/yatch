package score;

import dice.DiceSet;

public abstract class ScoreCategory {

    protected String name;

    public ScoreCategory(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public abstract int calculate(DiceSet diceSet);
}
