import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class BlobImageRenderer extends JLabel implements TableCellRenderer {

    private int width;
    private int height;

    // Puedes definir el tamaño deseado de la imagen
    public BlobImageRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof byte[]) {
            byte[] imageBytes = (byte[]) value;
            ImageIcon icon = new ImageIcon(imageBytes);
            // Escalar la imagen al tamaño deseado
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(scaledImage));
        } else {
            setIcon(null);
        }

        // Opcional: cambiar fondo si está seleccionado
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }

        return this;
    }
}
