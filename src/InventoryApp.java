import javax.swing.SwingUtilities;

public class InventoryApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Para probar, puedes pasar un usuario de prueba
            MainFrame mainFrame = new MainFrame("usuarioDePrueba");
            mainFrame.setVisible(true);
        });
    }
}