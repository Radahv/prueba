import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FormularioAsignacion extends JFrame {
    private JComboBox<String> cbUsuarios, cbMateriales;
    private JTextField txtCantidad;
    private JButton btnAsignar;

    public FormularioAsignacion() {
        setTitle("Asignar Material");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        // Etiquetas y componentes
        add(new JLabel("Seleccionar Usuario:"));
        cbUsuarios = new JComboBox<>();
        add(cbUsuarios);

        add(new JLabel("Seleccionar Material:"));
        cbMateriales = new JComboBox<>();
        add(cbMateriales);

        add(new JLabel("Cantidad a Asignar:"));
        txtCantidad = new JTextField();
        add(txtCantidad);

        btnAsignar = new JButton("Asignar");
        add(btnAsignar);

        // Cargar datos en ComboBox
        cargarUsuarios();
        cargarMateriales();

        // Acción del botón
        btnAsignar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                asignarMaterial();
            }
        });

        setVisible(true);
    }

    // Método para cargar usuarios en el ComboBox
    private void cargarUsuarios() {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre FROM usuarios")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                cbUsuarios.addItem(id + " - " + nombre); // Guardamos "id - nombre"
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios.");
        }
    }

    // Método para cargar materiales en el ComboBox
    private void cargarMateriales() {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT iD, nombre FROM inventario")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                cbMateriales.addItem(id + " - " + nombre); // Guardamos "id - nombre"
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar materiales.");
        }
    }

    // Método para asignar material
    private void asignarMaterial() {
        try (Connection con = DBConnection.getConnection()) {
            String usuarioSeleccionado = (String) cbUsuarios.getSelectedItem();
            String materialSeleccionado = (String) cbMateriales.getSelectedItem();
            int cantidadAsignada = Integer.parseInt(txtCantidad.getText());

            if (usuarioSeleccionado == null || materialSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un usuario y un material.");
                return;
            }

            // Obtener los ID
            int idUsuario = Integer.parseInt(usuarioSeleccionado.split(" - ")[0]);
            int idMaterial = Integer.parseInt(materialSeleccionado.split(" - ")[0]);

            // Verificar stock disponible
            String verificarCantidad = "SELECT cantidad FROM inventario WHERE id = ?";
            try (PreparedStatement psVerificar = con.prepareStatement(verificarCantidad)) {
                psVerificar.setInt(1, idMaterial);
                ResultSet rs = psVerificar.executeQuery();

                if (rs.next()) {
                    int cantidadDisponible = rs.getInt("cantidad");
                    if (cantidadAsignada > cantidadDisponible) {
                        JOptionPane.showMessageDialog(this, "No hay suficiente stock disponible.");
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Material no encontrado.");
                    return;
                }
            }

            // Insertar asignación
            String insertarAsignacion = "INSERT INTO asignaciones (id_usuario, id_inventario, cantidad_asignada, asignado_en) VALUES (?, ?, ?, NOW())";
            try (PreparedStatement psInsertar = con.prepareStatement(insertarAsignacion)) {
                psInsertar.setInt(1, idUsuario);
                psInsertar.setInt(2, idMaterial);
                psInsertar.setInt(3, cantidadAsignada);
                psInsertar.executeUpdate();
            }

            // Actualizar stock en materiales
            String actualizarMaterial = "UPDATE inventario SET cantidad = cantidad - ? WHERE id = ?";
            try (PreparedStatement psActualizar = con.prepareStatement(actualizarMaterial)) {
                psActualizar.setInt(1, cantidadAsignada);
                psActualizar.setInt(2, idMaterial);
                psActualizar.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Material asignado con éxito.");
            txtCantidad.setText("");

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al asignar material. Verifique los datos.");
        }
    }
}

