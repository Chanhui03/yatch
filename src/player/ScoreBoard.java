package player;

import game.ScoreCategory;
import java.util.HashMap;

public class ScoreBoard {

    private final HashMap<ScoreCategory, Integer> scores = new HashMap<>();

    //특정 점수 카테고리에 이미 점수가 기록되었는지 확인
    public boolean isUsed(ScoreCategory category) {
        return scores.containsKey(category);
    }
    //특정 점수 카테고리에 점수 기록
    public void recordScore(ScoreCategory category, int score) {
        scores.put(category, score);
    }
    //특정 점수 카테고리의 점수 조회
    public Integer getScore(ScoreCategory category) {
        return scores.get(category);
    }
    //기록된 모든 점수의 합계 계산
    public int getTotalScore() {
        int sum = 0;
        for (int v : scores.values()) {
            sum += v;
        }
        return sum;
    }
    //모든 점수 카테고리에 점수가 기록되었는지 확인
    public boolean isFull() {
        return scores.size() == ScoreCategory.ALL.size();
    }


}
