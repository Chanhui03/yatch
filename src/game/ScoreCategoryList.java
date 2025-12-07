package game;

import java.util.*;

public class ScoreCategoryList {
    //게임의 모든 점수 카테고리 목록을 리스트로 묶어서 제공하는 클래스
    //각 점수 항목은 ScoreCategory를 상속받아서 calculate 메소드를 구현함
    //이 클래스를 통해 UI나 GameManager에서 쉽게 접근 가능
    public static final List<ScoreCategory> ALL = List.of(
        new Aces(),
        new Twos(),
        new Threes(),
        new Fours(),
        new Fives(),
        new Sixes(),

        new Choice(),
        new FourOfAKind(),
        new FullHouse(),
        new SmallStraight(),
        new LargeStraight(),
        new Yacht()
    );
}
