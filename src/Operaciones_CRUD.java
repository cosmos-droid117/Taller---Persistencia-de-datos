
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Operaciones_CRUD {

    // RF1: Crear / Registrar
    public boolean registrarProducto(Producto p) {
        String sql = "INSERT INTO productos(codigo, nombre, descripcion, precio, cantidad) VALUES(?,?,?,?,?)";

        // El try-with-resources (paréntesis tras el try) asegura que la conexión se cierre automáticamente
        try (Connection conn = Conexion_Base_de_Datos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getCodigo());
            pstmt.setString(2, p.getNombre());
            pstmt.setString(3, p.getDescripcion());
            pstmt.setDouble(4, p.getPrecio());
            pstmt.setInt(5, p.getCantidad());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar: " + e.getMessage());
            return false;
        }
    }

    // RF2: Leer / Consultar Todos
    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Connection conn = Conexion_Base_de_Datos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar: " + e.getMessage());
        }
        return lista;
    }

    // RF2: Búsqueda específica
    public Producto buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM productos WHERE codigo = ?";
        Producto p = null;

        try (Connection conn = Conexion_Base_de_Datos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                p = new Producto(rs.getString("codigo"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getDouble("precio"),
                        rs.getInt("cantidad"));
            }
        } catch (SQLException e) {
            System.err.println("Error en búsqueda: " + e.getMessage());
        }
        return p;
    }

    // RF3: Actualizar
    public boolean actualizarProducto(Producto p) {
        String sql = "UPDATE productos SET precio = ?, descripcion = ?, cantidad = ? WHERE codigo = ?";

        try (Connection conn = Conexion_Base_de_Datos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, p.getPrecio());
            pstmt.setString(2, p.getDescripcion());
            pstmt.setInt(3, p.getCantidad());
            pstmt.setString(4, p.getCodigo());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // RF4: Eliminar
    public boolean eliminarProducto(String codigo) {
        String sql = "DELETE FROM productos WHERE codigo = ?";

        try (Connection conn = Conexion_Base_de_Datos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }


}