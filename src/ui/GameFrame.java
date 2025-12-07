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
    
    //GameFrame 생성자
    public GameFrame() {
        setTitle("Yacht Dice Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 640);
        setLocationRelativeTo(null);

        initGame();     // 여기서 "인원 수 + 불러오기" 다이얼로그 처리
        initUI();
        syncFromState();   // 초기 화면 동기화

        setVisible(true);
    }

    // -----------------------------
    // 시작 설정용 DTO
    // -----------------------------
    private static class StartConfig {
        final boolean loadExisting;
        final int playerCount;

        StartConfig(boolean loadExisting, int playerCount) {
            this.loadExisting = loadExisting;
            this.playerCount = playerCount;
        }
    }

    // -----------------------------
    // 게임 초기화 (일반용)
    // -----------------------------
    private void initGame() {
        // 아직 한 번도 시작한 적 없는 경우에만 다이얼로그 사용
        if (manager.getState() == null && playerCount == 0) {
            initGameFromDialog();
        } else {
            // 이미 playerCount가 정해져 있는 경우(같은 인원으로 새 게임) → 그대로 새 게임
            manager.startNewGame(playerCount);
        }
    }

    // -----------------------------
    // "인원 수 / 불러오기" 다이얼로그를 사용해 새 게임 시작
    // (처음 시작 + '인원 다시 입력' 둘 다 여기 사용)
    // -----------------------------
    private void initGameFromDialog() {
        while (true) {
            StartConfig cfg = askStartConfig();  // 인원 수 + 불러오기 버튼 포함 다이얼로그

            if (cfg.loadExisting) {
                // 세이브 불러오기 시도 (시작 전 → 인원 수 체크 X)
                if (manager.loadGame()) {
                    playerCount = manager.getPlayerCount();
                    return; // 불러오기에 성공하면 바로 종료
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "세이브 파일을 불러오지 못했습니다.\n새 게임을 시작하거나 다시 시도해주세요.",
                            "불러오기 오류",
                            JOptionPane.ERROR_MESSAGE
                    );
                    // while 루프를 돌면서 다시 다이얼로그 띄움
                }
            } else {
                // 새 게임
                playerCount = cfg.playerCount;
                manager.startNewGame(playerCount);
                return;
            }
        }
    }

    // -----------------------------
    // 인원 수 + 불러오기 선택 다이얼로그
    // -----------------------------
    private StartConfig askStartConfig() {
        final JDialog dialog = new JDialog(this, "플레이어 수 / 불러오기", true);

        JLabel label = new JLabel("플레이어 수 (1~4): ");
        JTextField playerField = new JTextField("2", 5);

        JButton startButton = new JButton("시작");
        JButton loadButton = new JButton("불러오기");
        JButton exitButton = new JButton("종료");

        final StartConfig[] result = new StartConfig[1];

        // 세이브 파일 없으면 불러오기 버튼 비활성화
        boolean canLoad = manager.hasSave();
        loadButton.setEnabled(canLoad);
        if (!canLoad) {
            loadButton.setToolTipText("저장된 게임이 없습니다.");
        }

        startButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(playerField.getText().trim());
                if (n >= 1 && n <= 4) {
                    result[0] = new StartConfig(false, n);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "플레이어 수는 1~4 사이여야 합니다.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "숫자를 입력하세요.");
            }
        });

        // 시작 전 불러오기: 여기서는 "불러오기 하겠다"라는 의지만 넘기고
        // 실제 loadGame 호출은 initGameFromDialog()가 담당
        loadButton.addActionListener(e -> {
            result[0] = new StartConfig(true, 0);
            dialog.dispose();
        });

        exitButton.addActionListener(e -> System.exit(0));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(label);
        inputPanel.add(playerField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loadButton);
        buttonPanel.add(startButton);
        buttonPanel.add(exitButton);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(inputPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);

        if (result[0] == null) {
            // 창을 강제로 닫는 상황은 없으니 안전하게 종료
            System.exit(0);
        }
        return result[0];
    }

    // -----------------------------
    // UI 구성
    // -----------------------------
    private void initUI() {
        setLayout(new BorderLayout());

        // 상단: 현재 플레이어 + 저장/불러오기
        JPanel topPanel = new JPanel(new BorderLayout());
        currentPlayerLabel.setFont(currentPlayerLabel.getFont().deriveFont(Font.BOLD, 18f));
        currentPlayerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(currentPlayerLabel, BorderLayout.WEST);

        // 오른쪽 상단: 저장 / 불러오기 버튼
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("게임 저장하기");
        JButton loadButton = new JButton("불러오기");

        saveButton.addActionListener(e -> {
            manager.saveGame();
            JOptionPane.showMessageDialog(
                    GameFrame.this,
                    "게임이 저장되었습니다.",
                    "저장",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        loadButton.addActionListener(e -> {
            if (!manager.hasSave()) {
                JOptionPane.showMessageDialog(
                        GameFrame.this,
                        "저장된 게임이 없습니다.",
                        "불러오기",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // 진행 중인 게임에서는 현재 인원 수와 같은 세이브만 허용
            boolean ok = manager.loadGameWithSamePlayerCountOnly();
            if (!ok) {
                JOptionPane.showMessageDialog(
                        GameFrame.this,
                        "현재 게임의 플레이어 수와 다른 세이브 파일입니다.\n"
                      + "같은 인원으로 저장된 게임만 불러올 수 있습니다.",
                        "플레이어 수 불일치",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            playerCount = manager.getPlayerCount();
            syncFromState();
            JOptionPane.showMessageDialog(
                    GameFrame.this,
                    "게임을 불러왔습니다.",
                    "불러오기",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        topRightPanel.add(saveButton);
        topRightPanel.add(loadButton);
        topPanel.add(topRightPanel, BorderLayout.EAST);

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

        // 새 게임 버튼 → 같은 인원 / 인원 다시 입력 / 취소 중 선택
        restartButton.addActionListener(e -> {
            Object[] options = {"같은 인원으로", "인원 다시 입력", "취소"};
            int choice = JOptionPane.showOptionDialog(
                    GameFrame.this,
                    "새 게임을 어떻게 시작할까요?",
                    "새 게임",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) { // 같은 인원으로
                int result = JOptionPane.showConfirmDialog(
                        GameFrame.this,
                        "현재 게임을 종료하고 같은 인원으로 새 게임을 시작할까요?",
                        "새 게임",
                        JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION) {
                    initGame();      // playerCount 그대로 사용
                    syncFromState();
                }
            } else if (choice == 1) { // 인원 다시 입력
                int result = JOptionPane.showConfirmDialog(
                        GameFrame.this,
                        "현재 게임을 종료하고 인원 수를 다시 설정할까요?",
                        "새 게임",
                        JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION) {
                    // 이전 상태/인원 수는 버리고, 다이얼로그부터 다시
                    manager.startNewGame(Math.max(playerCount, 1)); // 의미 없는 초기화 (원하면 생략 가능)
                    playerCount = 0;
                    initGameFromDialog();
                    syncFromState();
                }
            } else {
                // 취소 또는 닫기 → 아무 것도 안 함
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
                // 게임 끝났으면 클릭 무시
                if (manager.isGameFinished()) return;

                int row = scoreTable.rowAtPoint(e.getPoint());
                int col = scoreTable.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) return;

                // 카테고리 행이 아니거나, 0열(라벨 열)이면 비활성
                if (!scoreTableModel.isCategoryRow(row) || col == 0) {
                    return;
                }

                // 현재 플레이어 열이 아니면 비활성
                int playerIndex = col - 1;
                int currentIdx = manager.getState().getCurrentPlayerIndex();
                if (playerIndex != currentIdx) {
                    return;
                }

                // 아직 한 번도 굴리지 않았다면(모두 0) 비활성
                if (!hasRolledThisTurn()) {
                    return;
                }

                // 이미 사용한 카테고리라면 비활성
                ScoreCategory category = scoreTableModel.getCategoryAtRow(row);
                if (!manager.canUseCategory(category)) {
                    return;
                }

                // 여기까지 온 셀만 "활성 셀" → 선택 및 두 번째 클릭 처리
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
    // 이번 턴에서 한 번이라도 주사위를 굴렸는지 여부
    // (모두 0이면 아직 안 굴린 상태)
    // -----------------------------
    private boolean hasRolledThisTurn() {
        DiceSet diceSet = manager.getState().getDiceSet();
        List<Integer> values = diceSet.getValues();
        for (int v : values) {
            if (v != 0) {
                return true;
            }
        }
        return false;
    }

    // -----------------------------
    // 셀 두 번째 클릭 → 점수 기록
    // -----------------------------
    private void handleCellSecondClick(int row, int col) {
        // 턴 시작 직후(아직 한 번도 굴리지 않았거나 게임 끝난 경우)에는 점수 기록 불가
        if (!hasRolledThisTurn() || manager.isGameFinished()) {
            return;
        }

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
        currentPlayerLabel.setText("현재 플레이어 : Player " + (idx + 1));

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
                    if (playerIndex == currentIdx
                            && !manager.isGameFinished()
                            && GameFrame.this.hasRolledThisTurn()) {   // 굴린 후에만 미리보기
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
