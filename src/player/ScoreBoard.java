package player;

import game.ScoreCategory;

import java.util.EnumMap;
import java.util.Map;

public class ScoreBoard {

    private final Map<ScoreCategory, Integer> scores = new EnumMap<>(ScoreCategory.class);

    public boolean isUsed(ScoreCategory category) {
        return scores.containsKey(category);
    }

    public void recordScore(ScoreCategory category, int score) {
        scores.put(category, score);
    }

    public Integer getScore(ScoreCategory category) {
        return scores.get(category);
    }

    public int getTotalScore() {
        int sum = 0;
        for (int v : scores.values()) {
            sum += v;
        }
        return sum;
    }

    public boolean isFull() {
        return scores.size() == ScoreCategory.values().length;
    }
}
