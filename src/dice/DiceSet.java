package dice;

import java.util.ArrayList;
import java.util.List;

public class DiceSet {

    private final List<Dice> diceList;

    public DiceSet() {
        this(5);
    }

    public DiceSet(int count) {
        diceList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            diceList.add(new Dice());
        }
    }

    public void rollAll() {
        for (Dice dice : diceList) {
            dice.roll();
        }
    }

    public void resetAll() {
        for (Dice dice : diceList) {
            // 저장 데이터용 메서드 재활용 (값 0, held=false)
            dice.setFromSavedData(0, false);
        }
    }

    public void toggleHold(int index) {
        if (index < 0 || index >= diceList.size()) return;
        Dice dice = diceList.get(index);
        if (dice.isHeld()) {
            dice.release();
        } else {
            dice.hold();
        }
    }

    public List<Integer> getValues() {
        List<Integer> values = new ArrayList<>();
        for (Dice dice : diceList) {
            values.add(dice.getValue());
        }
        return values;
    }

    public boolean isHeld(int index) {
        return diceList.get(index).isHeld();
    }

    public int size() {
        return diceList.size();
    }
}
