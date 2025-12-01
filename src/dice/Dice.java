package dice;

import java.util.Random;


public class Dice {
    // 굴려진 주사위 값
    private int value;
    // 주사위를 랜덤하게 굴리기 위한 랜덤 변수
    private static final Random rand = new Random();
    // 주사위 홀딩 여부
    private boolean isHeld;

    // 주사위 생성자
    public Dice() {
        // 주사위 값 초기화
        this.value = 0;
        // 고정 여부 초기화
        this.isHeld = false;
    }

    /**
     * 주사위 굴리기
     */
    public void roll() {
        // 고정이면 굴리기 x
        if (isHeld) return;
        // 주사위 굴리기
        this.value = rand.nextInt(6) + 1;
    }

    /**
     * 주사위 고정하기
     */
    public void hold() {
        this.isHeld = true;
    }

    /**
     * 주사위 고정 해제
     */
    public void release() {
        this.isHeld = false;
    }

    /**
     * 주사위 홀딩 여부 가져오기
     * @return 주사위 홀딩 여부
     */
    public boolean isHeld() {
        return isHeld;
    }

    /**
     * 주사위 값 가져오기
     * @return 주사위 값
     */
    public int getValue() {
        return value;
    }
}
