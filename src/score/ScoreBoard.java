package score;

import java.util.*;

public class ScoreBoard {

    private HashMap<String, Integer> scores;

    public ScoreBoard() {
        scores = new HashMap<>();
    }

    public void setScore(String category, int score) {
        scores.put(category, score);
    }

    public Integer getScore(String category) {
        return scores.getOrDefault(category, null);
    }

    public void showScores() {
        // TODO: 점수판 출력
    }

    public Map<String, Integer> getAllScores() {
        return scores;
    }

}
