import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;

public class ImageUtil {

    public static ImageIcon getImageFromBlob(ResultSet rs, String columnName) {
        try {
            Blob blob = rs.getBlob(columnName);
            if(blob != null) {
                int blobLength = (int) blob.length();
                byte[] imageBytes = blob.getBytes(1, blobLength);
                blob.free();
                // Opcional: Escalar la imagen
                ImageIcon icon = new ImageIcon(imageBytes);
                return icon;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

