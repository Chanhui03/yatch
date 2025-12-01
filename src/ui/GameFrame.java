package ui;

import dice.DiceSet;
import game.GameManager;
import game.GameState;
import game.ScoreCategory;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import player.Player;

public class GameFrame extends JFrame {

    private final GameManager manager = new GameManager();

    private final JLabel currentPlayerLabel = new JLabel();
    private final JButton rollButton = new JButton("Roll");
    private final JButton restartButton = new JButton("새 게임");
    private final JToggleButton[] diceButtons = new JToggleButton[5];

    private ScoreTableModel scoreTableModel;
    private JTable scoreTable;

    // 우리가 직접 관리하는 선택된 셀
    private int selectedRow = -1;
    private int selectedCol = -1;

    // 새 게임 시 재사용할 플레이어 수
    private int playerCount = 0;

    public GameFrame() {
        setTitle("Yacht Dice Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 640);
        setLocationRelativeTo(null);

        initGame();
        initUI();
        syncFromState();   // 초기 화면 동기화

        setVisible(true);
    }

    // -----------------------------
    // 게임 초기화
    // -----------------------------
    private void initGame() {
        // 최초 1번만 물어보고, 이후에는 같은 인원으로 재시작
        if (playerCount == 0) {
            playerCount = askPlayerCount();
        }
        manager.startNewGame(playerCount);
        // ❗ 여기서는 절대 rollDice() 호출 안 함 → 처음엔 항상 ? 상태
        // 각 턴 시작 시 GameManager 내부에서
        // dice 값은 0, rerollsLeft = 3 으로 맞춰져 있어야 함.
    }

    private int askPlayerCount() {
        while (true) {
            String input = JOptionPane.showInputDialog(this, "플레이어 수를 입력하세요 (1~4)", "2");
            if (input == null) System.exit(0);
            try {
                int n = Integer.parseInt(input.trim());
                if (n >= 1 && n <= 4) return n;
            } catch (NumberFormatException ignored) {}
            JOptionPane.showMessageDialog(this, "1~4 사이의 숫자를 입력하세요.");
        }
    }

    // -----------------------------
    // UI 구성
    // -----------------------------
    private void initUI() {
        setLayout(new BorderLayout());

        // 상단: 현재 플레이어
        JPanel topPanel = new JPanel(new BorderLayout());
        currentPlayerLabel.setFont(currentPlayerLabel.getFont().deriveFont(Font.BOLD, 18f));
        currentPlayerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(currentPlayerLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // 중앙: 주사위 + 버튼들
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel dicePanel = new JPanel(new FlowLayout());

        for (int i = 0; i < diceButtons.length; i++) {
            final int idx = i;
            JToggleButton btn = new JToggleButton("?");
            btn.setFont(new Font("SansSerif", Font.BOLD, 20));
            btn.setPreferredSize(new Dimension(60, 60));
            btn.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
            btn.addActionListener(e -> {
                manager.toggleHold(idx);
                syncDiceFromState();
            });
            diceButtons[i] = btn;
            dicePanel.add(btn);
        }

        JPanel controlPanel = new JPanel(new FlowLayout());

        // Roll 버튼: 누를 때마다 1회 소모
        rollButton.addActionListener(e -> {
            manager.rollDice();   // GameManager에서 rerollsLeft 감소 + dice 굴림
            syncFromState();
        });

        // 새 게임 버튼
        restartButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    GameFrame.this,
                    "현재 게임을 종료하고 새 게임을 시작할까요?",
                    "새 게임",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                initGame();
                syncFromState();
            }
        });

        controlPanel.add(rollButton);
        controlPanel.add(restartButton);

        centerPanel.add(dicePanel, BorderLayout.CENTER);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // 우측: 점수판
        scoreTableModel = new ScoreTableModel(manager);
        scoreTable = new JTable(scoreTableModel);
        scoreTable.setRowHeight(24);

        // JTable 기본 selection 기능은 끔, 선택은 우리가 직접 관리
        scoreTable.setRowSelectionAllowed(false);
        scoreTable.setColumnSelectionAllowed(false);
        scoreTable.setCellSelectionEnabled(false);

        scoreTable.setDefaultRenderer(
                Object.class,
                new ScoreCellRenderer(manager, scoreTableModel)
        );

        // 클릭: 첫 클릭 → 선택 / 같은 셀 다시 클릭 → 점수 기록
        scoreTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = scoreTable.rowAtPoint(e.getPoint());
                int col = scoreTable.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) return;

                if (row == selectedRow && col == selectedCol) {
                    // 두 번째 클릭 → 점수 기록 시도
                    handleCellSecondClick(row, col);
                } else {
                    // 첫 클릭 → 선택만
                    selectedRow = row;
                    selectedCol = col;
                    scoreTable.repaint();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(scoreTable);
        scroll.setPreferredSize(new Dimension(380, 0));
        add(scroll, BorderLayout.EAST);
    }

    // -----------------------------
    // 셀 두 번째 클릭 → 점수 기록
    // -----------------------------
    private void handleCellSecondClick(int row, int col) {
        // 0열(카테고리 이름) 제외 + 카테고리 행만 가능
        if (!scoreTableModel.isCategoryRow(row) || col == 0) return;

        int playerIndex = col - 1;
        int currentIdx = manager.getState().getCurrentPlayerIndex();
        if (playerIndex != currentIdx) return; // 다른 플레이어 칸이면 무시

        ScoreCategory category = scoreTableModel.getCategoryAtRow(row);
        if (!manager.canUseCategory(category)) return; // 이미 사용한 카테고리면 무시

        // 확인창 없이 바로 점수 기록
        manager.applyScore(category);  // 내부에서 다음 플레이어/턴으로 넘어간다고 가정
        scoreTableModel.refresh();
        syncFromState();
    }

    // -----------------------------
    // 상태 동기화
    // -----------------------------
    private void syncFromState() {
        GameState state = manager.getState();

        // 현재 플레이어 표시
        int idx = state.getCurrentPlayerIndex();
        currentPlayerLabel.setText("현재 플레이어: Player " + (idx + 1));

        updateRollButtonLabel();
        syncDiceFromState();
        scoreTableModel.refresh();

        // 턴이 넘어갈 때 선택 초기화
        selectedRow = -1;
        selectedCol = -1;
        scoreTable.repaint();

        if (manager.isGameFinished()) {
            showFinalResult();
        }
    }

    private void syncDiceFromState() {
        DiceSet diceSet = manager.getState().getDiceSet();
        List<Integer> values = diceSet.getValues();

        for (int i = 0; i < diceButtons.length; i++) {
            int val = values.get(i);
            JToggleButton btn = diceButtons[i];

            // 값 0이면 아직 안 굴린 상태 → ? 표시 + 비활성
            if (val == 0) {
                btn.setText("?");
                btn.setEnabled(false);
                btn.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
            } else {
                btn.setText(String.valueOf(val));
                btn.setEnabled(true);
                if (diceSet.isHeld(i)) {
                    btn.setBorder(new LineBorder(Color.BLACK, 3));
                } else {
                    btn.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
                }
            }
        }
    }

    private void updateRollButtonLabel() {
        if (manager.isGameFinished()) {
            rollButton.setText("Roll");
            rollButton.setEnabled(false);
            return;
        }

        int left = manager.getState().getRerollsLeft(); // 턴당 총 3회라고 가정
        rollButton.setText("Roll (" + left + "회 남음)");
        rollButton.setEnabled(left > 0);
    }

    // -----------------------------
    // 게임 종료
    // -----------------------------
    private void showFinalResult() {
        StringBuilder sb = new StringBuilder("게임 종료!\n\n");
        int best = -1;
        String winner = "";

        for (int i = 0; i < manager.getPlayerCount(); i++) {
            Player p = manager.getPlayer(i);
            int total = p.getScoreBoard().getTotalScore();
            int bonus = scoreTableModel.getUpperTotalForPlayer(i) >= 63 ? 35 : 0;
            total += bonus;

            sb.append(p.getName()).append(": ").append(total).append("점\n");
            if (total > best) {
                best = total;
                winner = p.getName();
            }
        }
        sb.append("\n우승: ").append(winner).append(" (" + best + "점)");
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    // -----------------------------
    // 셀 렌더러 (요트 스타일 + 선택 셀만 테두리)
    // -----------------------------
    private class ScoreCellRenderer extends DefaultTableCellRenderer {

        private final GameManager manager;
        private final ScoreTableModel model;

        // 색상 팔레트
        private static final Color CATEGORY_HEADER_BG = new Color(45, 45, 45);
        private static final Color CATEGORY_HEADER_FG = Color.WHITE;
        private static final Color CATEGORY_CELL_BG = new Color(255, 248, 200);
        private static final Color SUMMARY_CELL_BG = new Color(220, 220, 220);
        private static final Color TOTAL_CELL_BG = new Color(255, 230, 150);
        private static final Color SELECT_BORDER = new Color(255, 165, 0);

        public ScoreCellRenderer(GameManager manager, ScoreTableModel model) {
            this.manager = manager;
            this.model = model;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {

            // JTable 기본 선택 효과는 끄고, 항상 동일하게 렌더링
            super.getTableCellRendererComponent(
                    table,
                    value,
                    false,
                    false,
                    row,
                    column
            );

            ScoreTableModel.RowType type = model.getRowType(row);

            setFont(table.getFont());
            setOpaque(true);
            setBorder(null);

            // 0번 열: 카테고리/라벨
            if (column == 0) {
                setFont(getFont().deriveFont(Font.BOLD));
                switch (type) {
                    case CATEGORY:
                        setBackground(CATEGORY_HEADER_BG);
                        setForeground(CATEGORY_HEADER_FG);
                        setText(model.getCategoryAtRow(row).getDisplayName());
                        break;
                    case SUBTOTAL:
                        setBackground(CATEGORY_HEADER_BG);
                        setForeground(CATEGORY_HEADER_FG);
                        setText("Subtotal");
                        break;
                    case BONUS:
                        setBackground(CATEGORY_HEADER_BG);
                        setForeground(CATEGORY_HEADER_FG);
                        setText("+35 Bonus");
                        break;
                    case BONUS_INFO:
                        setBackground(new Color(230, 230, 230));
                        setForeground(Color.DARK_GRAY);
                        setText("Bonus if upper section ≥ 63 points");
                        break;
                    case TOTAL:
                        setBackground(CATEGORY_HEADER_BG);
                        setForeground(CATEGORY_HEADER_FG);
                        setText("Total");
                        break;
                }

                if (row == selectedRow && column == selectedCol) {
                    setBorder(new LineBorder(SELECT_BORDER, 2));
                }
                return this;
            }

            // 플레이어 칸
            int playerIndex = column - 1;
            int upper = model.getUpperTotalForPlayer(playerIndex);

            if (type == ScoreTableModel.RowType.CATEGORY) {
                ScoreCategory category = model.getCategoryAtRow(row);
                Player player = manager.getPlayer(playerIndex);
                Integer recorded = player.getScoreBoard().getScore(category);

                setBackground(CATEGORY_CELL_BG);
                setForeground(Color.BLACK);

                if (recorded != null) {
                    setText(String.valueOf(recorded));
                } else {
                    int currentIdx = manager.getState().getCurrentPlayerIndex();
                    if (playerIndex == currentIdx && !manager.isGameFinished()) {
                        int preview = manager.previewScore(category);
                        setText(String.valueOf(preview));
                        setForeground(Color.GRAY);
                    } else {
                        setText("");
                    }
                }
            } else {
                // SUBTOTAL / BONUS / TOTAL / BONUS_INFO
                switch (type) {
                    case SUBTOTAL:
                        setBackground(SUMMARY_CELL_BG);
                        setForeground(upper >= 63 ? new Color(0, 128, 0) : Color.DARK_GRAY);
                        setText(upper + "/63");
                        break;

                    case BONUS:
                        setBackground(SUMMARY_CELL_BG.darker());
                        setForeground(Color.BLACK);
                        setText(upper >= 63 ? "35" : "");
                        break;

                    case BONUS_INFO:
                        setBackground(table.getBackground());
                        setForeground(Color.DARK_GRAY);
                        setText("");
                        break;

                    case TOTAL:
                        int base = manager.getPlayer(playerIndex).getScoreBoard().getTotalScore();
                        int bonus = (upper >= 63) ? 35 : 0;
                        setBackground(TOTAL_CELL_BG);
                        setForeground(Color.BLACK);
                        setText(String.valueOf(base + bonus));
                        break;
                }
            }

            // 선택된 셀만 주황 테두리
            if (row == selectedRow && column == selectedCol) {
                setBorder(new LineBorder(SELECT_BORDER, 2));
            }

            return this;
        }
    }
}
