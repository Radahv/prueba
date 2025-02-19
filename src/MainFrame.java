import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainFrame extends JFrame {
    private String usuarioLogueado;
    private JTabbedPane tabbedPane;
    private JTable tblInventario;
    private JTable tblUsuarios;
    private JButton btnNuevoItem; // Botón para agregar un nuevo item

    public MainFrame(String usuario) {
        this.usuarioLogueado = usuario;
        setTitle("Inventario - Usuario: " + usuarioLogueado);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        cargarDatosInventario();
        cargarDatosUsuarios();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Panel de Inventario
        JPanel panelInventario = new JPanel(new BorderLayout());
        tblInventario = new JTable();
        JScrollPane scrollInventario = new JScrollPane(tblInventario);
        panelInventario.add(scrollInventario, BorderLayout.CENTER);

        // Panel inferior para botones en el inventario
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNuevoItem = new JButton("Nuevo Item");
        panelBotones.add(btnNuevoItem);
        panelInventario.add(panelBotones, BorderLayout.SOUTH);

        btnNuevoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Abrir el formulario para insertar un nuevo item
                InsertInventoryItemFrame insertFrame = new InsertInventoryItemFrame();
                // Opcional: agregar un WindowListener para recargar la tabla al cerrar el formulario
                insertFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        // Recargar datos del inventario
                        cargarDatosInventario();
                    }
                });
                insertFrame.setVisible(true);
            }
        });

        // Panel de Usuarios
        JPanel panelUsuarios = new JPanel(new BorderLayout());
        tblUsuarios = new JTable();
        JScrollPane scrollUsuarios = new JScrollPane(tblUsuarios);
        panelUsuarios.add(scrollUsuarios, BorderLayout.CENTER);

        // Añadir pestañas al tabbedPane
        tabbedPane.addTab("Inventario", panelInventario);
        tabbedPane.addTab("Usuarios", panelUsuarios);

        add(tabbedPane);
    }

    // Método para cargar datos del inventario desde la base de datos
    private void cargarDatosInventario() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Tipo","Cantidad", "Imagen", "Detalles"}, 0);
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT id, nombre, tipo, cantidad, imagen, detalles FROM inventario";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");
                int cantidad = rs.getInt("cantidad");
                String detalles = rs.getString("detalles");

                // Leer el BLOB y convertirlo a arreglo de bytes
                Blob blob = rs.getBlob("imagen");
                byte[] imageBytes = null;
                if (blob != null) {
                    int blobLength = (int) blob.length();
                    imageBytes = blob.getBytes(1, blobLength);
                    blob.free();
                }
                model.addRow(new Object[]{id, nombre, tipo,cantidad, imageBytes, detalles});

                tblInventario.setModel(model);
                // Asigna el renderer para mostrar la imagen en la columna "Imagen"
                tblInventario.getColumnModel().getColumn(4).setCellRenderer(new BlobImageRenderer(150, 150));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar inventario: " + ex.getMessage());
        }

        // Ajusta el alto de las filas para que se adapte al tamaño de la imagen
        tblInventario.setRowHeight(150);

        // Opcional: ajusta el ancho de la columna de imagen
        tblInventario.getColumnModel().getColumn(4).setPreferredWidth(150);
    }

    // Método para cargar datos de usuarios (ejemplo básico)
    private void cargarDatosUsuarios() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Email"}, 0);
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT id, nombre, email FROM usuarios";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                model.addRow(new Object[]{id, nombre, email});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + ex.getMessage());
        }
        tblUsuarios.setModel(model);
    }
}
