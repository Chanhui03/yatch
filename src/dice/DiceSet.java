package dice;

public class DiceSet {
    // 주사위 세트 배열 크기 (고정 5개)
    private static final int SIZE = 5;
    // 주사위 세트 배열
    private Dice[] dices;

    /**
     * 주사위 세트 생성자
     */
    public DiceSet() {
        // 배열 생성
        dices = new Dice[SIZE];
        // 배열에 주사위 값 넣기
        for (int i = 0; i < SIZE; i++) {
            // 주사위 세트[i]에 새로운 주사위 객체 생성
            dices[i] = new Dice();
            // 주사위 세트[i] 주사위 굴리기
            dices[i].roll();
        }
    }

    /**
     * 주사위 모두 굴리기 (턴 시작 시)
     */
    public void rollAll() {
        for (Dice dice : dices) {
            dice.release();
            dice.roll();
        }
    }


    /**
     * 주사위 다시 굴리기 (턴 중)
     */
    public void reRoll() {
        for (Dice dice : dices) {
            dice.roll();
        }
    }

    /**
     * 주사위 hold 전환
     * @param index 주사위 번호
     */
    public void toggleHold(int index) {
        Dice dice = dices[index];
        if (dice.isHeld()) dice.release();
        else dice.hold();
    }

    /**
     * 주사위 세트 가져오기
     * @return 주사위 세트
     */
    public Dice[] getDices() {
        return dices;
    }
}
