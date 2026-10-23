import java.util.List;
import java.util.Scanner;

public class Interfaz_GUI {
    private Operaciones_CRUD dao;
    private Scanner scanner;

    public Interfaz_GUI() {
        dao = new Operaciones_CRUD();
        scanner = new Scanner(System.in);
        if (!Conexion_Base_de_Datos.inicializarBaseDeDatos()) {
            System.err.println("No se pudo inicializar la base de datos. Cerrando.");
            System.exit(1);
        }
    }

    public void iniciar() {
        int opcion = 0;
        do {
            System.out.println("\n=== SISTEMA DE GESTIÓN DE PRODUCTOS ===");
            System.out.println("1. Registrar Producto");
            System.out.println("2. Consultar todos los productos");
            System.out.println("3. Buscar producto por código o nombre");
            System.out.println("4. Actualizar producto");
            System.out.println("5. Eliminar producto");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String entrada = leerLinea();
            if (entrada == null) break; // fin de la entrada estándar
            try {
                opcion = Integer.parseInt(entrada.trim());
                procesarOpcion(opcion);
            } catch (NumberFormatException e) {
                opcion = 0;
                System.out.println("Por favor, ingrese un número válido.");
            }
        } while (opcion != 6);
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1: registrar(); break;
            case 2: consultarTodos(); break;
            case 3: buscar(); break;
            case 4: actualizar(); break;
            case 5: eliminar(); break;
            case 6: System.out.println("Cerrando sistema. ¡Hasta luego!"); break;
            default: System.out.println("Opción no válida.");
        }
    }

    private String leerLinea() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    private String leerTextoObligatorio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String s = leerLinea();
            if (s == null) throw new IllegalStateException("Fin de la entrada");
            s = s.trim();
            if (!s.isEmpty()) return s;
            System.out.println("Error: este campo es obligatorio y no puede estar vacío.");
        }
    }

    private Double leerPrecio(String mensaje, boolean opcional) {
        while (true) {
            System.out.print(mensaje);
            String s = leerLinea();
            if (s == null) throw new IllegalStateException("Fin de la entrada");
            s = s.trim();
            if (s.isEmpty() && opcional) return null;
            try {
                double v = Double.parseDouble(s.replace(',', '.'));
                if (Double.isFinite(v) && v >= 0) return v;
            } catch (NumberFormatException e) {
                // se informa abajo
            }
            System.out.println("Dato inválido: ingrese un número mayor o igual a 0.");
        }
    }

    private Integer leerCantidad(String mensaje, boolean opcional) {
        while (true) {
            System.out.print(mensaje);
            String s = leerLinea();
            if (s == null) throw new IllegalStateException("Fin de la entrada");
            s = s.trim();
            if (s.isEmpty() && opcional) return null;
            try {
                int v = Integer.parseInt(s);
                if (v >= 0) return v;
            } catch (NumberFormatException e) {
                // se informa abajo
            }
            System.out.println("Dato inválido: ingrese un número entero mayor o igual a 0.");
        }
    }

    private void registrar() {
        System.out.println("\n-- REGISTRAR PRODUCTO --");
        String codigo = leerTextoObligatorio("Código: ");

        if (dao.buscarPorCodigo(codigo) != null) {
            System.out.println("Error: Ya existe un producto con ese código.");
            return;
        }

        String nombre = leerTextoObligatorio("Nombre: ");
        String desc = leerTextoObligatorio("Descripción: ");
        double precio = leerPrecio("Precio (debe ser mayor o igual a 0): ", false);
        int cantidad = leerCantidad("Cantidad disponible (entero mayor o igual a 0): ", false);

        Producto p = new Producto(codigo, nombre, desc, precio, cantidad);
        if (dao.registrarProducto(p)) {
            System.out.println("¡Producto registrado exitosamente!");
        } else {
            System.out.println("No se pudo registrar el producto.");
        }
    }

    private void consultarTodos() {
        System.out.println("\n-- LISTA DE PRODUCTOS --");
        mostrarLista(dao.obtenerTodos(), "No hay productos registrados.");
    }

    private void buscar() {
        String texto = leerTextoObligatorio("\nIngrese el código o el nombre a buscar: ");
        mostrarLista(dao.buscarPorCodigoONombre(texto), "Producto no encontrado.");
    }

    private void mostrarLista(List<Producto> lista, String mensajeVacio) {
        if (lista.isEmpty()) {
            System.out.println(mensajeVacio);
        } else {
            for (Producto p : lista) {
                System.out.println(p.toString());
            }
            System.out.println(lista.size() + " producto(s).");
        }
    }

    private void actualizar() {
        String codigo = leerTextoObligatorio("\nIngrese el código del producto a actualizar: ");
        Producto p = dao.buscarPorCodigo(codigo);

        if (p == null) {
            System.out.println("Producto no encontrado.");
            return;
        }

        System.out.println("Actualizando: " + p.getNombre());
        System.out.print("Nueva descripción (Enter para mantener la actual): ");
        String desc = leerLinea();
        if (desc == null) throw new IllegalStateException("Fin de la entrada");
        if (!desc.trim().isEmpty()) p.setDescripcion(desc.trim());

        Double precio = leerPrecio("Nuevo precio (Enter para mantener el actual): ", true);
        if (precio != null) p.setPrecio(precio);

        Integer cantidad = leerCantidad("Nueva cantidad (Enter para mantener la actual): ", true);
        if (cantidad != null) p.setCantidad(cantidad);

        if (dao.actualizarProducto(p)) {
            System.out.println("Producto actualizado exitosamente.");
        } else {
            System.out.println("No se pudo actualizar el producto.");
        }
    }

    private void eliminar() {
        String codigo = leerTextoObligatorio("\nIngrese el código del producto a eliminar: ");
        Producto p = dao.buscarPorCodigo(codigo);

        if (p == null) {
            System.out.println("Producto no encontrado.");
            return;
        }

        System.out.println("Producto encontrado: " + p.getNombre());
        String confirmacion;
        do {
            System.out.print("¿Está seguro que desea eliminarlo? (S/N): ");
            confirmacion = leerLinea();
            if (confirmacion == null) throw new IllegalStateException("Fin de la entrada");
            confirmacion = confirmacion.trim();
        } while (!confirmacion.equalsIgnoreCase("S") && !confirmacion.equalsIgnoreCase("N"));

        if (confirmacion.equalsIgnoreCase("S")) {
            if (dao.eliminarProducto(codigo)) {
                System.out.println("Producto eliminado.");
            } else {
                System.out.println("No se pudo eliminar el producto.");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    public static void main(String[] args) {
        try {
            new Interfaz_GUI().iniciar();
        } catch (IllegalStateException e) {
            System.out.println("\nEntrada finalizada. Cerrando sistema.");
        }
    }
}
