package ui;

import game.GameManager;
import game.ScoreCategory;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import player.Player;

public class ScoreTableModel extends AbstractTableModel {

    public enum RowType {
        CATEGORY,
        SUBTOTAL,
        BONUS,
        BONUS_INFO,
        TOTAL
    }

    private static class Row {
        RowType type;
        ScoreCategory category; // CATEGORY일 때만 사용
        String label;           // 나머지 행용 텍스트

        Row(RowType type, ScoreCategory category, String label) {
            this.type = type;
            this.category = category;
            this.label = label;
        }
    }

    private final GameManager manager;
    private final List<Row> rows = new ArrayList<>();

    public ScoreTableModel(GameManager manager) {
        this.manager = manager;

        // 상단 섹션
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.ACES, null));
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.TWOS, null));
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.THREES, null));
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.FOURS, null));
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.FIVES, null));
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.SIXES, null));

        // Subtotal / Bonus / 설명
        rows.add(new Row(RowType.SUBTOTAL, null, "Subtotal"));
        rows.add(new Row(RowType.BONUS, null, "+35 Bonus"));
        rows.add(new Row(RowType.BONUS_INFO, null, "Bonus if upper section ≥ 63 points"));

        // 하단 섹션
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.CHOICE, null));
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.FOUR_OF_A_KIND, null));
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.FULL_HOUSE, null));
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.SMALL_STRAIGHT, null));
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.LARGE_STRAIGHT, null));
        rows.add(new Row(RowType.CATEGORY, ScoreCategory.YACHT, null));

        // Total
        rows.add(new Row(RowType.TOTAL, null, "Total"));
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        // Category 컬럼 + 플레이어 수
        return 1 + manager.getPlayerCount();
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) return "Categories";
        return "Player " + column;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Row row = rows.get(rowIndex);

        if (columnIndex == 0) {
            if (row.type == RowType.CATEGORY) {
                return row.category.getDisplayName();
            } else {
                return row.label;
            }
        }

        int playerIndex = columnIndex - 1;
        Player player = manager.getPlayer(playerIndex);

        switch (row.type) {
            case CATEGORY:
                Integer s = player.getScoreBoard().getScore(row.category);
                return (s == null) ? "" : s;

            case SUBTOTAL: {
                int total = getUpperTotalForPlayer(playerIndex);
                // "47/63" 이런 식으로 표시
                return total + "/63";
            }

            case BONUS: {
                int upper = getUpperTotalForPlayer(playerIndex);
                return (upper >= 63) ? 35 : 0;
            }

            case BONUS_INFO:
                return "";

            case TOTAL: {
                int base = player.getScoreBoard().getTotalScore();
                int bonus = getUpperTotalForPlayer(playerIndex) >= 63 ? 35 : 0;
                return base + bonus;
            }

            default:
                return "";
        }
    }

    // 상단(Aces~Sixes) 합계 계산
    public int getUpperTotalForPlayer(int playerIndex) {
        Player player = manager.getPlayer(playerIndex);
        int total = 0;
        for (ScoreCategory c : new ScoreCategory[]{
                ScoreCategory.ACES, ScoreCategory.TWOS, ScoreCategory.THREES,
                ScoreCategory.FOURS, ScoreCategory.FIVES, ScoreCategory.SIXES
        }) {
            Integer s = player.getScoreBoard().getScore(c);
            if (s != null) total += s;
        }
        return total;
    }

    public RowType getRowType(int row) {
        return rows.get(row).type;
    }

    public boolean isCategoryRow(int row) {
        return rows.get(row).type == RowType.CATEGORY;
    }

    public ScoreCategory getCategoryAtRow(int row) {
        Row r = rows.get(row);
        return r.type == RowType.CATEGORY ? r.category : null;
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
