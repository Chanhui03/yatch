import javax.swing.SwingUtilities;
import ui.GameFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}
