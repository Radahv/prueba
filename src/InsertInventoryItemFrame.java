import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class InsertInventoryItemFrame extends JFrame {
    private JTextField txtNombre;
    private JTextField txtTipo;
    private JTextField txtCantidad;
    private JLabel lblImagen;
    private JTextField txtDetalles;
    private JButton btnSeleccionarImagen;
    private JButton btnGuardar;

    // Variable para guardar el archivo de imagen seleccionado
    private File imagenFile = null;

    public InsertInventoryItemFrame() {
        setTitle("Insertar Nuevo Item de Inventario");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);

        txtNombre = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(txtNombre, gbc);

        // Tipo
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Tipo:"), gbc);

        txtTipo = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(txtTipo, gbc);

        // Cantidad
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Cantidad:"), gbc);

        txtCantidad = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(txtCantidad, gbc);

        // Imagen
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Imagen:"), gbc);

        lblImagen = new JLabel("Ningún archivo seleccionado");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(lblImagen, gbc);

        // Detalles
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);

        txtDetalles = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(txtDetalles, gbc);

        // Botón para seleccionar imagen
        btnSeleccionarImagen = new JButton("Seleccionar Imagen");
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(btnSeleccionarImagen, gbc);

        btnSeleccionarImagen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seleccionarImagen();
            }
        });

        // Botón Guardar
        btnGuardar = new JButton("Guardar");
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(btnGuardar, gbc);

        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarItem();
            }
        });

        add(panel);
    }

    // Método para seleccionar la imagen usando JFileChooser
    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Opcional: agregar un filtro para imágenes
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "jpg", "png", "gif", "jpeg"));

        int resultado = fileChooser.showOpenDialog(this);
        if(resultado == JFileChooser.APPROVE_OPTION) {
            imagenFile = fileChooser.getSelectedFile();
            lblImagen.setText(imagenFile.getName());
        }
    }

    // Método para validar y guardar el item en la base de datos
    private void guardarItem() {
        String nombre = txtNombre.getText().trim();
        String tipo = txtTipo.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();
        String detalles = txtDetalles.getText().trim();

        if(nombre.isEmpty() || cantidadStr.isEmpty() || imagenFile == null) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos y seleccione una imagen.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número.");
            return;
        }

        InventoryDAO dao = new InventoryDAO();
        dao.insertarItemConImagen(nombre, tipo,cantidad, imagenFile, detalles);
        // Opcional: limpiar los campos después de insertar
        limpiarCampos();
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtCantidad.setText("");
        lblImagen.setText("Ningún archivo seleccionado");
        imagenFile = null;
    }

    // Método main para probar el formulario de inserción
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InsertInventoryItemFrame().setVisible(true);
        });
    }
}

