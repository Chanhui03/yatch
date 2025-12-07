package game;

import java.util.*;

public class ScoreCalculator {

    public static int calculate(ScoreCategory category, List<Integer> dice) {
        switch (category) {
            case ACES:   return sumOf(dice, 1);
            case TWOS:   return sumOf(dice, 2);
            case THREES: return sumOf(dice, 3);
            case FOURS:  return sumOf(dice, 4);
            case FIVES:  return sumOf(dice, 5);
            case SIXES:  return sumOf(dice, 6);
            case CHOICE: return sumAll(dice);
            case FOUR_OF_A_KIND: return fourOfAKind(dice);
            case FULL_HOUSE:     return fullHouse(dice);
            case SMALL_STRAIGHT: return smallStraight(dice);
            case LARGE_STRAIGHT: return largeStraight(dice);
            case YACHT:          return yacht(dice);
            default:             return 0;
        }
    }

    private static int sumOf(List<Integer> dice, int face) {
        int sum = 0;
        for (int d : dice) {
            if (d == face) sum += d;
        }
        return sum;
    }

    private static int sumAll(List<Integer> dice) {
        int sum = 0;
        for (int d : dice) sum += d;
        return sum;
    }

    private static HashMap<Integer, Integer> countFaces(List<Integer> dice) {
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (int d : dice) {
            counts.put(d, counts.getOrDefault(d, 0) + 1);
        }
        return counts;
    }

    private static int fourOfAKind(List<Integer> dice) {
        HashMap<Integer, Integer> counts = countFaces(dice);
        for (int count : counts.values()) {
            if (count >= 4) {
                return sumAll(dice);
            }
        }
        return 0;
    }

    private static int fullHouse(List<Integer> dice) {
        HashMap<Integer, Integer> counts = countFaces(dice);
        boolean hasThree = false;
        boolean hasTwo = false;
        for (int c : counts.values()) {
            if (c == 3) hasThree = true;
            else if (c == 2) hasTwo = true;
        }
        return (hasThree && hasTwo) ? 25 : 0;
    }

    private static int smallStraight(List<Integer> dice) {
        List<Integer> set = dice;
        if (set.containsAll(Arrays.asList(1, 2, 3, 4)) ||
            set.containsAll(Arrays.asList(2, 3, 4, 5)) ||
            set.containsAll(Arrays.asList(3, 4, 5, 6))) {
            return 15;
        }
        return 0;
    }

    private static int largeStraight(List<Integer> dice) {
        List<Integer> set = dice;
        if (set.containsAll(Arrays.asList(1, 2, 3, 4, 5)) ||
            set.containsAll(Arrays.asList(2, 3, 4, 5, 6))) {
            return 30;
        }
        return 0;
    }

    private static int yacht(List<Integer> dice) {
        int first = dice.get(0);
        for (int d : dice) {
            if (d != first) return 0;
        }
        return 50;
    }
}
