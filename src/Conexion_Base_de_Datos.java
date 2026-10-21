import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion_Base_de_Datos {
    // La base de datos se guardará en un archivo local llamado "inventario.db"
    private static final String URL = "jdbc:sqlite:inventario.db";

    // Lanza SQLException si no hay conexión (antes devolvía null y provocaba NullPointerException)
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Crea la tabla si no existe. Devuelve false si no se pudo inicializar.
    public static boolean inicializarBaseDeDatos() {
        String sql = "CREATE TABLE IF NOT EXISTS productos ("
                + "codigo TEXT PRIMARY KEY CHECK (length(trim(codigo)) > 0),"
                + "nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0),"
                + "descripcion TEXT,"
                + "precio REAL NOT NULL CHECK (precio >= 0),"
                + "cantidad INTEGER NOT NULL CHECK (cantidad >= 0)"
                + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al inicializar la tabla: " + e.getMessage());
            return false;
        }
    }
}
