import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class InventoryDAO {

    public void insertarItemConImagen(String nombre, String tipo,int cantidad, File imagenFile, String detalles) {
        String sql = "INSERT INTO inventario (nombre, tipo, cantidad, imagen, detalles) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             FileInputStream fis = new FileInputStream(imagenFile);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, nombre);
            pst.setString(2, tipo);
            pst.setInt(3, cantidad);
            pst.setBinaryStream(4, fis, (int) imagenFile.length());
            pst.setString(5, detalles);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Item insertado correctamente.");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Archivo no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar: " + e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

