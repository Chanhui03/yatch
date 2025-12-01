package score;

import dice.DiceSet;

public class Aces extends ScoreCategory {

    public Aces() {
        super("ACES");
    }

    @Override
    public int calculate(DiceSet set) {
        int sum = 0;
        for (var d : set.getDices()) { // DiceSet 기준
            if (d.getValue() == 1) sum += 1;
        }
        return sum;
    }
}
