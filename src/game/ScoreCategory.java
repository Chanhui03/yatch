package game;

public enum ScoreCategory {
    ACES("Aces (1)"),
    TWOS("Twos (2)"),
    THREES("Threes (3)"),
    FOURS("Fours (4)"),
    FIVES("Fives (5)"),
    SIXES("Sixes (6)"),
    CHOICE("Choice"),
    FOUR_OF_A_KIND("Four of a Kind"),
    FULL_HOUSE("Full House"),
    SMALL_STRAIGHT("Small Straight"),
    LARGE_STRAIGHT("Large Straight"),
    YACHT("Yacht");

    private final String displayName;

    ScoreCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
