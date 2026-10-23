# Sistema de Gestión de Productos

Aplicación de consola en Java para registrar y administrar los productos de un pequeño establecimiento comercial. Los datos se guardan en una base de datos SQLite mediante JDBC, por lo que permanecen almacenados después de cerrar y reiniciar el programa.

**Taller de Programación — Universidad Manuela Beltrán**
Autor: Ángel Samuel Peña Camargo · Docente: Juan José Osorio Tabares

## Contenido

1. [Funcionalidades](#funcionalidades)
2. [Requisitos](#requisitos)
3. [Estructura del proyecto](#estructura-del-proyecto)
4. [Arquitectura](#arquitectura)
5. [Ejecución rápida (ejecutable)](#ejecución-rápida-ejecutable)
6. [Compilar y ejecutar desde el código](#compilar-y-ejecutar-desde-el-código)
7. [Uso del programa](#uso-del-programa)
8. [Validaciones y manejo de errores](#validaciones-y-manejo-de-errores)
9. [Base de datos](#base-de-datos)
10. [Pruebas](#pruebas)
11. [Solución de problemas](#solución-de-problemas)
12. [Tecnologías](#tecnologías)
13. [Enlaces](#enlaces)

## Funcionalidades

| Requerimiento | Descripción |
|---|---|
| RF1 | Registrar productos (código, nombre, descripción, precio y cantidad) |
| RF2 | Consultar todos los productos y buscar por código o nombre |
| RF3 | Actualizar precio, descripción o cantidad de un producto |
| RF4 | Eliminar un producto por su código, con confirmación |
| RF5 | Persistir la información en SQLite (archivo `inventario.db`) |

## Requisitos

- **Java 17 o superior** (JRE o JDK). Es lo único necesario para usar el ejecutable.
- **Maven 3.8 o superior** solo si se va a compilar desde el código (IntelliJ IDEA trae uno integrado).
- Conexión a internet la primera vez que se compila, para descargar el driver `sqlite-jdbc`.

Para comprobar Java: abrir una terminal y ejecutar `java -version`.

## Estructura del proyecto

```
.
├── pom.xml                              Configuración de Maven (dependencias y empaquetado)
├── README.md
├── .gitignore
├── Ejecutable/
│   ├── gestion-productos-1.0.jar        Programa empaquetado con el driver incluido
│   └── Ejecutar.bat                     Lanzador con doble clic (Windows)
└── src/main/java/
    ├── Producto.java                    Modelo
    ├── Conexion_Base_de_Datos.java      Conexión JDBC y creación de la tabla
    ├── Operaciones_CRUD.java            Acceso a datos (único lugar donde se escribe SQL)
    └── Interfaz_GUI.java                Menú de consola y validaciones
```

Al ejecutar el programa se genera `inventario.db` en la carpeta desde la que se lanza. Las carpetas `target/` y `out/` y los archivos `*.db` están excluidos del repositorio en `.gitignore`.

## Arquitectura

El proyecto se organiza en tres capas, siguiendo el patrón DAO:

| Clase | Capa | Responsabilidad |
|---|---|---|
| `Producto` | Modelo | Representa un producto (código, nombre, descripción, precio, cantidad) |
| `Conexion_Base_de_Datos` | Acceso a datos | Entrega la conexión JDBC (`conectar`) y crea la tabla si no existe (`inicializarBaseDeDatos`) |
| `Operaciones_CRUD` | Acceso a datos | Operaciones CRUD con `PreparedStatement` |
| `Interfaz_GUI` | Presentación | Menú, lectura y validación de datos, y llamadas al DAO |

Operaciones implementadas en `Operaciones_CRUD`:

| Operación | Requisito | Método |
|---|---|---|
| Crear | RF1 | `registrarProducto` |
| Consultar todos | RF2 | `obtenerTodos` |
| Consultar por código | RF2 | `buscarPorCodigo` |
| Consultar por código o nombre | RF2 | `buscarPorCodigoONombre` |
| Actualizar | RF3 | `actualizarProducto` |
| Eliminar | RF4 | `eliminarProducto` |

Todas las consultas usan `PreparedStatement` (los valores se envían como parámetros, sin concatenar SQL) y bloques `try-with-resources`, que cierran conexiones y recursos automáticamente.

## Ejecución rápida (ejecutable)

1. Verificar que la carpeta `Ejecutable/` contenga `gestion-productos-1.0.jar` y `Ejecutar.bat` juntos.
2. Hacer doble clic en **`Ejecutar.bat`**. Se abre la consola con el menú.
3. Al salir del programa, la ventana espera una tecla antes de cerrarse.

> No se debe abrir el `.jar` directamente con doble clic: en Windows se ejecuta sin consola y el menú no se ve. Por eso se incluye `Ejecutar.bat`.

El archivo `inventario.db` se crea en la misma carpeta del ejecutable y conserva los datos entre ejecuciones.

## Compilar y ejecutar desde el código

### Desde la terminal

Ubicarse en la raíz del proyecto (donde está `pom.xml`):

```bash
mvn clean package
java -jar target/gestion-productos-1.0.jar
```

El primer comando compila el proyecto y genera `target/gestion-productos-1.0.jar`. Para obtener la carpeta `Ejecutable/`, copiar ese jar junto con `Ejecutar.bat`.

### Desde IntelliJ IDEA

1. Abrir el proyecto y cargar Maven (*Load Maven Changes*).
2. Abrir `Interfaz_GUI.java` y pulsar *Run* junto al método `main`.

Para generar el jar desde IntelliJ: pestaña **Maven** → *gestion-productos* → *Lifecycle* → doble clic en `clean` y luego en `package`.

## Uso del programa

Al iniciar se muestra el menú:

```
=== SISTEMA DE GESTIÓN DE PRODUCTOS ===
1. Registrar Producto
2. Consultar todos los productos
3. Buscar producto por código o nombre
4. Actualizar producto
5. Eliminar producto
6. Salir
```

- **Registrar:** pide código, nombre, descripción, precio y cantidad. No permite códigos repetidos.
- **Consultar:** lista todos los productos ordenados por código.
- **Buscar:** acepta código o nombre, con coincidencia parcial.
- **Actualizar:** modifica descripción, precio y cantidad; presionar Enter conserva el valor actual.
- **Eliminar:** muestra el producto y pide confirmación (S/N) antes de borrarlo.

## Validaciones y manejo de errores

| Situación | Comportamiento |
|---|---|
| Opción de menú no numérica o inexistente | Muestra un aviso y vuelve al menú |
| Código, nombre o descripción vacíos | Se repite la pregunta hasta recibir un valor |
| Precio negativo o no numérico | Se repite la pregunta (acepta coma o punto decimal) |
| Cantidad negativa, decimal o no numérica | Se repite la pregunta |
| Código duplicado al registrar | Muestra «Ya existe un producto con ese código» |
| Producto inexistente al buscar, actualizar o eliminar | Muestra «Producto no encontrado» |
| Respuesta distinta de S/N al eliminar | Se repite la pregunta |
| Error de conexión o de SQL | Se captura `SQLException`, se informa y el programa no se cierra |
| Cierre de la entrada (Ctrl+D / Ctrl+Z) | El programa termina de forma ordenada |

Las validaciones se aplican en tres niveles: la interfaz, el DAO (`esValido`) y la base de datos (restricciones `CHECK`).

## Base de datos

Tabla `productos` en el archivo local `inventario.db` (se crea automáticamente al iniciar):

```sql
CREATE TABLE IF NOT EXISTS productos (
    codigo TEXT PRIMARY KEY CHECK (length(trim(codigo)) > 0),
    nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
    descripcion TEXT,
    precio REAL NOT NULL CHECK (precio >= 0),
    cantidad INTEGER NOT NULL CHECK (cantidad >= 0)
);
```

Si existe un `inventario.db` de una versión anterior, debe borrarse para que se genere con la estructura actual (la tabla solo se crea si no existe). Para inspeccionarlo se puede usar IntelliJ (pestaña *Database*) o DB Browser for SQLite.

## Pruebas

Casos verificados manualmente (evidencias en el documento técnico):

| N.º | Caso | Resultado esperado |
|---|---|---|
| 1 | Registrar con datos válidos | Producto guardado |
| 2 | Registrar con código repetido | Mensaje de error, sin duplicar |
| 3 | Registrar con campos vacíos | Se pide el dato de nuevo |
| 4 | Registrar con precio negativo | Se pide el precio de nuevo |
| 5 | Registrar con cantidad negativa o decimal | Se pide la cantidad de nuevo |
| 6 | Listar todos | Se muestran todos los registros |
| 7 | Buscar por código existente | Se muestra el producto |
| 8 | Buscar por nombre | Se muestran las coincidencias |
| 9 | Buscar un producto inexistente | «Producto no encontrado» |
| 10 | Actualizar precio, descripción o cantidad | Cambios reflejados al consultar |
| 11 | Actualizar un código inexistente | «Producto no encontrado» |
| 12 | Eliminar y confirmar | El producto desaparece |
| 13 | Eliminar y cancelar | El producto permanece |
| 14 | Cerrar y reiniciar | Los datos siguen almacenados |

## Solución de problemas

| Problema | Causa y solución |
|---|---|
| «'java' no se reconoce como un comando» | Java no está instalado o no está en el PATH. Instalar JDK 17+ |
| El `.bat` dice que no encuentra el jar | `Ejecutar.bat` y el `.jar` deben estar en la misma carpeta |
| «No suitable driver» al ejecutar desde IntelliJ | Maven no cargó las dependencias: *Reload project* en la pestaña Maven |
| Al abrir el `.jar` con doble clic no pasa nada | Es normal: usar `Ejecutar.bat` |
| La base de datos no tiene las restricciones `CHECK` | Es un `inventario.db` antiguo: borrarlo y volver a ejecutar |
| IntelliJ pide «Download missing SQLite driver files» al abrir la base | Pulsar *Fix*; solo afecta la herramienta *Database*, no al programa |
| Caracteres extraños (tildes) en la consola | Ejecutar con `Ejecutar.bat`, que configura la codificación UTF-8 |

## Tecnologías

Java 17 · Maven · SQLite · JDBC (`org.xerial:sqlite-jdbc` 3.46.1.3) · IntelliJ IDEA · Git y GitHub

## Enlaces

- Repositorio: _pendiente: pegar URL de GitHub_
- Video explicativo (inglés, 4 a 6 minutos): _pendiente: pegar URL de YouTube_
