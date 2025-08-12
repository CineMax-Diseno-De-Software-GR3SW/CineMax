# 🎬 CineMax


CineMax es una aplicación de escritorio desarrollada para la gestión integral de un sistema de cines. Permite administrar empleados, roles, ventas de boletos, reportes, facturación y más, todo desde una interfaz moderna y fácil de usar.

## Características principales

- 👥 **Gestión de empleados**: Registro, edición, activación/desactivación y asignación de roles.
- 🛡️ **Control de roles y permisos**: Sistema flexible para definir permisos y roles personalizados.
- 🎟️ **Venta de boletos**: Registro y gestión de ventas, generación de boletos en PDF.
- 📊 **Reportes y facturación**: Generación de reportes de ventas y facturas en PDF/CSV.
- 🔒 **Seguridad**: Autenticación de usuarios y gestión de contraseñas.
- 🖥️ **Interfaz gráfica**: UI desarrollada con JavaFX y FXML, con temas personalizables.


## 🗂️ Estructura de directorios

```
CineMax/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/cinemax/
│       │       ├── empleados/           # Módulo de empleados y roles
│       │       ├── peliculas/           # Módulo de gestión de películas
│       │       ├── salas/               # Módulo de gestión de salas y butacas
│       │       ├── venta_boletos/       # Módulo de venta de boletos
│       │       ├── reportes/            # Módulo de reportes y facturación
│       │       ├── utilidades/          # Utilidades y conexiones (DB, Firebase)
│       │       └── Main.java            # Clase principal de la aplicación
│       └── resources/
│           ├── vistas/                  # Archivos FXML y recursos de UI
│           ├── temas/                   # Hojas de estilos CSS
│           └── imagenes/                # Imágenes y logos
├── lib/                                # Dependencias externas (ej. postgresql-42.7.3.jar)
├── Reportes/                           # Reportes y facturas generados
├── PDFsGenerados_BoletoFactura/        # Boletos y facturas generados en PDF
├── Documentacion/                      # Documentación y diagramas
├── pom.xml                             # Configuración Maven
└── README.md                           # Este archivo
```


## 🛠️ Tecnologías utilizadas

- ☕ **Java 17+**
- 🖥️ **JavaFX** (FXML, CSS)
- 🗄️ **PostgreSQL** (JDBC)
- 📦 **Maven** (gestión de dependencias)
- ☁️ **Firebase Storage** (opcional, para almacenamiento de archivos)


## 📋 Requisitos previos

- Java JDK 17 o superior
- Maven
- PostgreSQL (con la base de datos configurada)
- Conexión a Internet para Firebase Storage (si se usa)


## 🚀 Instalación y ejecución

1. **Clona el repositorio**  
   ```sh
   git clone https://github.com/CineMax-Diseno-De-Software-GR3SW/CineMax.git
   cd CineMax
   ```

2. **Configura la base de datos**  
   Edita los parámetros de conexión en `ConexionBaseSingleton`.

3. **Instala dependencias**  
   ```sh
   mvn install
   ```

4. **Ejecuta la aplicación**  
   ```sh
   mvn javafx:run
   ```
   O ejecuta la clase principal:
   ```sh
   java -cp "lib/*;target/classes" com.cinemax.Main
   ```

## 🖼️ Previsualización

- **Pantalla de Login:**
   ![Logo CineMax](https://firebasestorage.googleapis.com/v0/b/cinemax-b6023.firebasestorage.app/o/readMe%2FLogin.jpg?alt=media)
   _Ejemplo: Pantalla de inicio Login._

- **Portal Principal / Dashboard:**
   ![Íconos de módulos](https://firebasestorage.googleapis.com/v0/b/cinemax-b6023.firebasestorage.app/o/readMe%2FPortalPrincipal.jpg?alt=media)
   _Ejemplo: Portal Principal, gestión de módulos._ 

- **Gestión de Películas:**
   ![Portada de película](https://firebasestorage.googleapis.com/v0/b/cinemax-b6023.firebasestorage.app/o/readMe%2FGPeliculas.jpg?alt=media)
   _Ejemplo: Pantalla de gestión de películas (operaciones CRUD)._

- **Gestión de Salas y Butacas:**
   ![Plano de sala](https://firebasestorage.googleapis.com/v0/b/cinemax-b6023.firebasestorage.app/o/readMe%2FGSalas.jpg?alt=media)
   _Ejemplo:  Pantalla de gestión de salas y butacas (operaciones CRUD)._

- **Venta de Boletos:**
   ![Boleto ilustrativo](https://firebasestorage.googleapis.com/v0/b/cinemax-b6023.firebasestorage.app/o/readMe%2FGBoletos.jpg?alt=media)
   _Ejemplo:  Pantalla durante el proceso de venta de un boleto._

- **Reportes y Facturación:**
   ![Gráfica de reportes](https://firebasestorage.googleapis.com/v0/b/cinemax-b6023.firebasestorage.app/o/readMe%2FGReportes.jpg?alt=media)
   _Ejemplo:  Pantalla de gestión de reportes (operaciones CRUD)._

- Inicia sesión con tus credenciales.
- Accede al portal principal para gestionar usuarios, roles, ventas y reportes.
- Utiliza los menús para navegar entre las distintas funcionalidades.
- Los archivos generados (boletos, facturas, reportes) se guardan en los directorios correspondientes.

Consulta la carpeta `Documentacion` para diagramas y guías de uso.


## 📝 Licencia


Proyecto académico para la materia “Diseño de Software”. Uso educativo.

---


💡 ¿Dudas o sugerencias? Contacta al equipo de desarrollo.
