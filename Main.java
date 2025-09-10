import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {

         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Garante que a interface gráfica seja criada e atualizada na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            ImageEditor editor = new ImageEditor();
            editor.setVisible(true);
        });
    }
}