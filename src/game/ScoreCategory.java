package game;

import java.util.*;

public abstract class ScoreCategory {
    private final String displayName;

    //생성된 모든 ScoreCategory 객체 목록 저장
    public static final List<ScoreCategory> ALL = new ArrayList<>();

    public ScoreCategory(String displayName) {
        this.displayName = displayName;
        ALL.add(this);
    }

    public String getDisplayName() {
        return displayName;
    }

    //각 점수 카테고리 계산을 정의하는 메소드
    public abstract int calculate(List<Integer> dice);

    //특정 주사위 눈을 모두 더하는 메소드
    protected int sumOf(List<Integer> dice, int face) {
        int sum = 0;
        for (int d : dice) if (d == face) sum += d;
        return sum;
    }

    //전체 주사위의 합을 계산하는 메소드(Choice, Four of a Kind 등에서 사용)
    protected int sumAll(List<Integer> dice) {
        int sum = 0;
        for (int d : dice) sum += d;
        return sum;
    }
    
    //주사위에 나온 숫자들의 빈도수를 Map으로 반환
    //예: [1,2,2,1,5] -> {1:2, 2:2, 5:1}
    protected Map<Integer, Integer> countFaces(List<Integer> dice) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int d : dice) counts.put(d, counts.getOrDefault(d, 0) + 1);
        return counts;
    }
}


//실제 점수 카테고리 구현(public을 제거하여 동일 파일안에 사용)

class Aces extends ScoreCategory {
    public Aces() { super("Aces (1)"); }
    @Override public int calculate(List<Integer> dice) { return sumOf(dice, 1); }
}

class Twos extends ScoreCategory {
    public Twos() { super("Twos (2)"); }
    @Override public int calculate(List<Integer> dice) { return sumOf(dice, 2); }
}

class Threes extends ScoreCategory {
    public Threes() { super("Threes (3)"); }
    @Override public int calculate(List<Integer> dice) { return sumOf(dice, 3); }
}

class Fours extends ScoreCategory {
    public Fours() { super("Fours (4)"); }
    @Override public int calculate(List<Integer> dice) { return sumOf(dice, 4); }
}

class Fives extends ScoreCategory {
    public Fives() { super("Fives (5)"); }
    @Override public int calculate(List<Integer> dice) { return sumOf(dice, 5); }
}

class Sixes extends ScoreCategory {
    public Sixes() { super("Sixes (6)"); }
    @Override public int calculate(List<Integer> dice) { return sumOf(dice, 6); }
}

// CHOICE
class Choice extends ScoreCategory {
    public Choice() { super("Choice"); }
    @Override public int calculate(List<Integer> dice) { return sumAll(dice); }
}

// FOUR OF A KIND: 같은 숫자 4개 이상 나올 때 합 / 아니면 0점
class FourOfAKind extends ScoreCategory {
    public FourOfAKind() { super("Four of a Kind"); }
    @Override
    public int calculate(List<Integer> dice) {
        Map<Integer, Integer> counts = countFaces(dice);
        for (int c : counts.values()) if (c >= 4) return sumAll(dice);
        return 0;
    }
}

// FULL HOUSE: 3개와 2개 조합일 때 25점 / 아니면 0점
class FullHouse extends ScoreCategory {
    public FullHouse() { super("Full House"); }
    @Override
    public int calculate(List<Integer> dice) {
        Map<Integer, Integer> counts = countFaces(dice);
        boolean three = false, two = false;
        for (int c : counts.values()) {
            if (c == 3) three = true;
            else if (c == 2) two = true;
        }
        return (three && two) ? 25 : 0;
    }
}

// SMALL STRAIGHT: 4개 연속 숫자일 때 15점 / 아니면 0점
class SmallStraight extends ScoreCategory {
    public SmallStraight() { super("Small Straight"); }
    @Override
    public int calculate(List<Integer> dice) {
        List<Integer> set = dice;
        return (
            set.containsAll(Arrays.asList(1,2,3,4)) ||
            set.containsAll(Arrays.asList(2,3,4,5)) ||
            set.containsAll(Arrays.asList(3,4,5,6))
        ) ? 15 : 0;
    }
}

// LARGE STRAIGHT: 5개 연속 숫자일 때 30점 / 아니면 0점
class LargeStraight extends ScoreCategory {
    public LargeStraight() { super("Large Straight"); }
    @Override
    public int calculate(List<Integer> dice) {
        List<Integer> set = dice;
        return (
            set.containsAll(Arrays.asList(1,2,3,4,5)) ||
            set.containsAll(Arrays.asList(2,3,4,5,6))
        ) ? 30 : 0;
    }
}

// YACHT: 모두 같은 숫자일 때 50점 / 아니면 0점
class Yacht extends ScoreCategory {
    public Yacht() { super("Yacht"); }
    @Override
    public int calculate(List<Integer> dice) {
        int first = dice.get(0);
        for (int d : dice) if (d != first) return 0;
        return 50;
    }
}

