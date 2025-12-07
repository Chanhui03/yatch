package ui;

import game.GameManager;
import game.ScoreCategory;
import game.ScoreCategorySettings;
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
        for (ScoreCategory c : ScoreCategorySettings.UPPER) {
            rows.add(new Row(RowType.CATEGORY, c, null));
        }

        rows.add(new Row(RowType.SUBTOTAL, null, "Subtotal"));
        rows.add(new Row(RowType.BONUS, null, "+35 Bonus"));
        rows.add(new Row(RowType.BONUS_INFO, null, "Bonus if upper section ≥ 63 points"));

        // 하단 섹션
        for (ScoreCategory c : ScoreCategorySettings.LOWER) {
            rows.add(new Row(RowType.CATEGORY, c, null));
        }

        rows.add(new Row(RowType.TOTAL, null, "Total"));
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return 1 + manager.getPlayerCount(); // category + players
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) return "Categories";
        return "Player " + column;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Row row = rows.get(rowIndex);

        // 첫 번째 컬럼 = 카테고리명 또는 label
        if (columnIndex == 0) {
            if (row.type == RowType.CATEGORY)
                return row.category.getDisplayName();
            else
                return row.label;
        }

        // 플레이어 점수
        int playerIndex = columnIndex - 1;
        Player player = manager.getPlayer(playerIndex);

        switch (row.type) {
            case CATEGORY: {
                Integer score = player.getScoreBoard().getScore(row.category);
                return (score == null) ? "" : score;
            }

            case SUBTOTAL: {
                int total = getUpperTotalForPlayer(playerIndex);
                return total + "/63";
            }

            case BONUS: {
                return (getUpperTotalForPlayer(playerIndex) >= 63) ? 35 : 0;
            }

            case BONUS_INFO:
                return "";

            case TOTAL: {
                int base = player.getScoreBoard().getTotalScore();
                int bonus = (getUpperTotalForPlayer(playerIndex) >= 63) ? 35 : 0;
                return base + bonus;
            }
        }

        return "";
    }

    // 상단(Aces~Sixes) 합계 계산
    public int getUpperTotalForPlayer(int playerIndex) {
        Player player = manager.getPlayer(playerIndex);
        int total = 0;

        for (ScoreCategory c : ScoreCategorySettings.UPPER) {
            Integer s = player.getScoreBoard().getScore(c);
            if (s != null)
                total += s;
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
        return (r.type == RowType.CATEGORY) ? r.category : null;
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
