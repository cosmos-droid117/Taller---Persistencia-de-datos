import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Operaciones_CRUD {

    private boolean esValido(Producto p) {
        return p != null
                && p.getCodigo() != null && !p.getCodigo().trim().isEmpty()
                && p.getNombre() != null && !p.getNombre().trim().isEmpty()
                && Double.isFinite(p.getPrecio()) && p.getPrecio() >= 0
                && p.getCantidad() >= 0;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(rs.getString("codigo"), rs.getString("nombre"),
                rs.getString("descripcion"), rs.getDouble("precio"), rs.getInt("cantidad"));
    }

    public boolean registrarProducto(Producto p) {
        if (!esValido(p)) {
            System.err.println("Error al registrar: datos inválidos.");
            return false;
        }
        String sql = "INSERT INTO productos(codigo, nombre, descripcion, precio, cantidad) VALUES(?,?,?,?,?)";

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

    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY codigo";

        try (Connection conn = Conexion_Base_de_Datos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar: " + e.getMessage());
        }
        return lista;
    }

    public Producto buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM productos WHERE codigo = ?";

        try (Connection conn = Conexion_Base_de_Datos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en búsqueda: " + e.getMessage());
        }
        return null;
    }

    public List<Producto> buscarPorCodigoONombre(String texto) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE codigo LIKE ? OR nombre LIKE ? ORDER BY codigo";

        try (Connection conn = Conexion_Base_de_Datos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String patron = "%" + texto + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en búsqueda: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarProducto(Producto p) {
        if (!esValido(p)) {
            System.err.println("Error al actualizar: datos inválidos.");
            return false;
        }
        String sql = "UPDATE productos SET precio = ?, descripcion = ?, cantidad = ? WHERE codigo = ?";

        try (Connection conn = Conexion_Base_de_Datos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, p.getPrecio());
            pstmt.setString(2, p.getDescripcion());
            pstmt.setInt(3, p.getCantidad());
            pstmt.setString(4, p.getCodigo());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProducto(String codigo) {
        String sql = "DELETE FROM productos WHERE codigo = ?";

        try (Connection conn = Conexion_Base_de_Datos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }
}
