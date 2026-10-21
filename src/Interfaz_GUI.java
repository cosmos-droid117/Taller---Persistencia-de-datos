import java.util.List;
import java.util.Scanner;

public class Interfaz_GUI {
    private Operaciones_CRUD dao;
    private Scanner scanner;

    public Interfaz_GUI() {
        dao = new Operaciones_CRUD();
        scanner = new Scanner(System.in);
        // Inicializamos la base de datos al arrancar
        Conexion_Base_de_Datos.inicializarBaseDeDatos();
    }

    public void iniciar() {
        int opcion = 0;
        do {
            System.out.println("\n=== SISTEMA DE GESTIÓN DE PRODUCTOS ===");
            System.out.println("1. Registrar Producto");
            System.out.println("2. Consultar todos los productos");
            System.out.println("3. Buscar producto por código");
            System.out.println("4. Actualizar producto");
            System.out.println("5. Eliminar producto");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                procesarOpcion(opcion);
            } catch (NumberFormatException e) {
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

    private void registrar() {
        System.out.println("\n-- REGISTRAR PRODUCTO --");
        System.out.print("Código: ");
        String codigo = scanner.nextLine();

        if (dao.buscarPorCodigo(codigo) != null) {
            System.out.println("Error: Ya existe un producto con ese código.");
            return;
        }

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Descripción: ");
        String desc = scanner.nextLine();

        // Validación de precio (Condición Técnica 4)
        double precio = -1;
        do {
            System.out.print("Precio (debe ser mayor o igual a 0): ");
            try {
                precio = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Dato inválido.");
            }
        } while (precio < 0);

        // Validación de cantidad (Condición Técnica 4)
        int cantidad = -1;
        do {
            System.out.print("Cantidad disponible (entero mayor o igual a 0): ");
            try {
                cantidad = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Dato inválido.");
            }
        } while (cantidad < 0);

        Producto p = new Producto(codigo, nombre, desc, precio, cantidad);
        if (dao.registrarProducto(p)) {
            System.out.println("¡Producto registrado exitosamente!");
        }
    }

    private void consultarTodos() {
        System.out.println("\n-- LISTA DE PRODUCTOS --");
        List<Producto> lista = dao.obtenerTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay productos registrados.");
        } else {
            for (Producto p : lista) {
                System.out.println(p.toString());
            }
        }
    }

    private void buscar() {
        System.out.print("\nIngrese el código del producto a buscar: ");
        String codigo = scanner.nextLine();
        Producto p = dao.buscarPorCodigo(codigo);

        if (p != null) {
            System.out.println("Producto encontrado: " + p.toString());
        } else {
            System.out.println("Producto no encontrado.");
        }
    }

    private void actualizar() {
        System.out.print("\nIngrese el código del producto a actualizar: ");
        String codigo = scanner.nextLine();
        Producto p = dao.buscarPorCodigo(codigo);

        if (p == null) {
            System.out.println("Producto no encontrado.");
            return;
        }

        System.out.println("Actualizando: " + p.getNombre());
        System.out.print("Nueva descripción (deje en blanco para mantener la actual): ");
        String desc = scanner.nextLine();
        if (!desc.trim().isEmpty()) p.setDescripcion(desc);

        System.out.print("Nuevo precio (-1 para mantener el actual): ");
        double precio = Double.parseDouble(scanner.nextLine());
        if (precio >= 0) p.setPrecio(precio);

        System.out.print("Nueva cantidad (-1 para mantener la actual): ");
        int cantidad = Integer.parseInt(scanner.nextLine());
        if (cantidad >= 0) p.setCantidad(cantidad);

        if (dao.actualizarProducto(p)) {
            System.out.println("Producto actualizado exitosamente.");
        }
    }

    private void eliminar() {
        System.out.print("\nIngrese el código del producto a eliminar: ");
        String codigo = scanner.nextLine();
        Producto p = dao.buscarPorCodigo(codigo);

        if (p == null) {
            System.out.println("Producto no encontrado.");
            return;
        }

        System.out.println("Producto encontrado: " + p.getNombre());
        System.out.print("¿Está seguro que desea eliminarlo? (S/N): ");
        String confirmacion = scanner.nextLine();

        if (confirmacion.equalsIgnoreCase("S")) {
            if (dao.eliminarProducto(codigo)) {
                System.out.println("Producto eliminado.");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    public static void main(String[] args) {
        Interfaz_GUI ui = new Interfaz_GUI();
        ui.iniciar();
    }


}