package score;

import java.util.HashMap;
import java.util.Map;

public class ScoreBoard {

    private Map<String, Integer> scores;

    public ScoreBoard() {
        scores = new HashMap<>();
    }

    public void setScore(String category, int value) {
        scores.put(category, value);
    }

    public Integer getScore(String category) {
        return scores.getOrDefault(category, null);
    }

    public Map<String, Integer> getAllScores() {
        return scores;
    }

    public boolean isCategoryFilled(String category) {
        return scores.containsKey(category);
    }
}
