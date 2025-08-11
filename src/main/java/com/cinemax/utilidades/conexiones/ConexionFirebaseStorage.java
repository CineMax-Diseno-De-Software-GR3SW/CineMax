package com.cinemax.comun.conexiones;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

/**
 * Servicio para manejar operaciones con Firebase Storage.
 *
 * <p>Esta clase se encarga de la inicialización de la conexión con Firebase
 * y proporciona métodos para subir archivos, como imágenes de películas.</p>
 *
 * <p>Utiliza el patrón Singleton para asegurar una única instancia
 * de la conexión a Firebase durante el ciclo de vida de la aplicación.</p>
 *
 * @author GR3SW
 * @version 1.2
 */
public class ConexionFirebaseStorage {

    private static ConexionFirebaseStorage instancia;
    private Storage storage;
    private final String BUCKET_NAME = "cinemax-b6023.firebasestorage.app";

    /**
     * Constructor privado para el patrón Singleton.
     * Inicializa la conexión con Firebase Storage.
     */
    private ConexionFirebaseStorage() {
        try {
            // Nombre del archivo de credenciales que debe estar en 'src/main/resources'
            final String NOMBRE_ARCHIVO_CREDENCIALES = "serviceAccountKey.json";

            // Carga las credenciales como un recurso del classpath (la forma correcta)
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(NOMBRE_ARCHIVO_CREDENCIALES);
            if (inputStream == null) {
                throw new IOException("No se pudo encontrar el archivo de credenciales '" + NOMBRE_ARCHIVO_CREDENCIALES + "' en la carpeta 'src/main/resources'.");
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);

            // Opciones de Firebase (necesarias para obtener el bucket correctamente)
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setStorageBucket(BUCKET_NAME)
                .build();
            
            // Inicializa la app de Firebase solo si no existe una ya
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            // Inicializa el cliente de Cloud Storage
            this.storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        } catch (IOException e) {
            System.err.println("Error CRÍTICO al inicializar Firebase Storage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la instancia única del servicio (Singleton).
     *
     * @return La instancia de ConexionFirebaseStorage.
     */
    public static synchronized ConexionFirebaseStorage getInstancia() {
        if (instancia == null) {
            instancia = new ConexionFirebaseStorage();
        }
        return instancia;
    }

    /**
     * Sube un archivo de imagen a Firebase Storage y devuelve la URL pública funcional.
     *
     * @param archivoLocal El archivo (File) de la imagen a subir.
     * @return La URL pública y accesible de la imagen subida.
     * @throws IOException Si ocurre un error durante la lectura del archivo o la subida.
     */
    public String subirImagenYObtenerUrl(File archivoLocal) throws IOException {
        if (storage == null) {
            throw new IOException("El servicio de Storage no está inicializado.");
        }
        
        // Generar un nombre de archivo único para evitar colisiones
        String extension = obtenerExtension(archivoLocal.getName());
        String nombreArchivoUnico = "peliculas/" + UUID.randomUUID().toString() + "." + extension;

        // Configurar metadatos del archivo (importante para que el navegador lo interprete como imagen)
        BlobId blobId = BlobId.of(BUCKET_NAME, nombreArchivoUnico);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(Files.probeContentType(archivoLocal.toPath()))
                .build();

        // Subir el archivo
        try (InputStream inputStream = new FileInputStream(archivoLocal)) {
            storage.create(blobInfo, inputStream.readAllBytes());
        }

        // --- SECCIÓN CORREGIDA Y FINAL ---
        try {
            // IMPORTANTE: Codificar el nombre del archivo para que sea seguro en una URL.
            // Esto convierte "peliculas/archivo.png" en "peliculas%2Farchivo.png"
            String nombreArchivoCodificado = java.net.URLEncoder.encode(nombreArchivoUnico, "UTF-8");

            // Construir la URL pública con el formato correcto de la API de Firebase Storage
            return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media", BUCKET_NAME, nombreArchivoCodificado);

        } catch (java.io.UnsupportedEncodingException e) {
            // Este error es muy improbable con UTF-8, pero es bueno manejarlo
            throw new IOException("Error al codificar el nombre del archivo para la URL.", e);
        }
    }

    /**
     * Extrae la extensión de un nombre de archivo.
     * @param nombreArchivo El nombre del archivo.
     * @return La extensión sin el punto.
     */
    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1);
    }
}
