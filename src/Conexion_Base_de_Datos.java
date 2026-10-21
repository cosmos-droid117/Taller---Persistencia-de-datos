import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion_Base_de_Datos {
    // La base de datos se guardará en un archivo local llamado "inventario.db"
    private static final String URL = "jdbc:sqlite:inventario.db";

    public static Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
        return conexion;
    }

    // Método para crear la tabla si no existe al arrancar el programa (Actividad 1 y 2)
    public static void inicializarBaseDeDatos() {
        String sql = "CREATE TABLE IF NOT EXISTS productos ("
                + "codigo TEXT PRIMARY KEY,"
                + "nombre TEXT NOT NULL,"
                + "descripcion TEXT,"
                + "precio REAL NOT NULL,"
                + "cantidad INTEGER NOT NULL"
                + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error al inicializar la tabla: " + e.getMessage());
        }
    }


}