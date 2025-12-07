package game;

import java.util.List;

public class ScoreCategorySettings {
        //점수 카테고리들을 상단과 하단으로 나누어 리스트로 제공하는 클래스

//---------------------상단 점수 카테고리--------------------------------------
    public static final ScoreCategory ACES = new Aces();
    public static final ScoreCategory TWOS = new Twos();
    public static final ScoreCategory THREES = new Threes();
    public static final ScoreCategory FOURS = new Fours();
    public static final ScoreCategory FIVES = new Fives();
    public static final ScoreCategory SIXES = new Sixes();
//---------------------하단 점수 카테고리--------------------------------------
    public static final ScoreCategory CHOICE = new Choice();
    public static final ScoreCategory FOUR_OF_A_KIND = new FourOfAKind();
    public static final ScoreCategory FULL_HOUSE = new FullHouse();
    public static final ScoreCategory SMALL_STRAIGHT = new SmallStraight();
    public static final ScoreCategory LARGE_STRAIGHT = new LargeStraight();
    public static final ScoreCategory YACHT = new Yacht();

    //UI에 상단 테이블을 구성할 때 사용
    //보너스 점수를 계산하기 위해 분류
    public static final List<ScoreCategory> UPPER = List.of(
        ACES, TWOS, THREES, FOURS, FIVES, SIXES
    );
    
    //UI에 하단 테이블을 구성할 때 사용
    public static final List<ScoreCategory> LOWER = List.of(
        CHOICE, FOUR_OF_A_KIND, FULL_HOUSE,
        SMALL_STRAIGHT, LARGE_STRAIGHT, YACHT
    );
}
