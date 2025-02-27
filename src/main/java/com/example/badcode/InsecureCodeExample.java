package com.example.securityhotspots;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
// Para usar HTTPS en lugar de HTTP, importamos:
import javax.net.ssl.HttpsURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;  // Uso de SecureRandom si necesitamos generar tokens/llaves
import java.util.Arrays;
import java.util.Base64;

// Opcional si usaras servlets:
// import javax.servlet.http.Cookie;
// import javax.servlet.http.HttpServletResponse;

public class SecurityHotspotsExample {

    /**
     * 1. Eliminamos la llave/credencial embebida (hardcoded) 
     *    y, en su lugar, ofrecemos un método para cargarla 
     *    de una configuración segura o de variables de entorno.
     */
    // private static final String HARD_CODED_KEY = "mySecretAESKey"; // Eliminado

    public static void main(String[] args) {
        try {
            // Reflection corregido: limitamos las clases permitidas a un whitelist
            safeReflectionExample("java.lang.String");

            // Uso de SHA-256 en lugar de MD5
            secureHash("textoEjemplo");

            // Evitamos loguear contraseñas reales; en su lugar, las enmascaramos.
            logSensitiveData("password123");

            // Leer variables de entorno es normal, pero evitamos imprimir secretos
            safeEnvVarUsage();

            // Uso de HTTPS en lugar de HTTP
            secureHttpsConnection();

            // Ejemplo: si usas un contenedor Servlet, podrías probar createSecureCookie(...)
            // createSecureCookie(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ejemplo de cargar una clave desde un método seguro (no se embebe en el código).
     */
    private static String getEncryptionKey() {
        // Este método podría acceder a un gestor de configuraciones seguro 
        // o usar variables de entorno. Aquí simulamos una lectura "dummy".
        String key = System.getenv("APP_ENCRYPTION_KEY");
        if (key == null || key.isEmpty()) {
            // En producción, maneja adecuadamente la ausencia de la variable
            throw new IllegalStateException("No se encontró la clave de encriptación en variables de entorno");
        }
        return key;
    }

    /**
     * 2. Reflexión segura: restringimos las clases que se pueden cargar
     *    mediante un "whitelist" para minimizar el riesgo.
     */
    public static void safeReflectionExample(String className) throws Exception {
        // Definimos un conjunto de clases permitidas
        final String[] allowedClasses = {
            "java.lang.String",
            "java.lang.Integer",
            "java.util.ArrayList"
            // Agrega las que tu aplicación realmente necesite
        };

        // Verificamos que className esté en la lista blanca
        if (!Arrays.asList(allowedClasses).contains(className)) {
            throw new SecurityException("Intento de cargar una clase no permitida: " + className);
        }

        // Si pasó la validación, se permite la reflexión
        Class<?> clazz = Class.forName(className);
        Object instance = clazz.getDeclaredConstructor().newInstance();
        System.out.println("Reflexión invocada de forma segura en la clase: " + className + ". Instancia: " + instance);
    }

    /**
     * 3. Uso de algoritmo de hash más robusto (SHA-256 en lugar de MD5).
     */
    public static void secureHash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        String base64Hash = Base64.getEncoder().encodeToString(hash);
        System.out.println("Hash SHA-256: " + base64Hash);
    }

    /**
     * 4. Logging de datos sensibles de forma enmascarada.
     */
    public static void logSensitiveData(String password) {
        // En lugar de imprimir la contraseña en texto plano,
        // podemos "enmascararla" o eliminarla de los logs.
        String masked = maskPassword(password);
        System.out.println("Se recibió contraseña (enmascarada): " + masked);
    }

    /**
     * Ejemplo de enmascarado de contraseña, mostrando solo 2 caracteres.
     */
    private static String maskPassword(String password) {
        if (password == null || password.length() <= 2) {
            return "***";
        }
        return password.substring(0, 2) + "****";
    }

    /**
     * 5. Lectura de variables de entorno de forma segura.
     *    No imprimimos el contenido si es sensible.
     */
    public static void safeEnvVarUsage() {
        String secret = System.getenv("APP_SECRET");
        if (secret != null) {
            // En lugar de imprimir, solo mostramos un mensaje de confirmación.
            System.out.println("Variable de entorno APP_SECRET detectada (no se muestra en logs).");
        } else {
            System.out.println("No se encontró variable de entorno APP_SECRET.");
        }
    }

    /**
     * 6. Conexión HTTPS en lugar de HTTP para proteger la información en tránsito.
     */
    public static void secureHttpsConnection() throws IOException {
        // Suponiendo que "example.com" soporta HTTPS.
        URL url = new URL("https://example.com/login?user=admin&pass=admin123");
        
        // Nota: En un entorno real, no incluyas credenciales en la URL (parte query).
        // Usa POST con cuerpo, cabeceras seguras o un token con expiración.
        
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Código de respuesta HTTPS: " + responseCode);

        // Leer la respuesta si es necesario
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Procesar la respuesta...
                // Aquí solo imprimimos con fines de demostración
                System.out.println(line);
            }
        }
    }

    /**
     * 7. Creación de cookies con banderas de seguridad (opcional).
     *
     * Descomenta si usas servlets y tienes la dependencia en tu proyecto:
     *
     * public static void createSecureCookie(HttpServletResponse response) {
     *     Cookie cookie = new Cookie("sessionId", "12345");
     *     cookie.setPath("/");
     *     // Aseguramos la cookie
     *     cookie.setHttpOnly(true);  // Previene acceso desde JavaScript
     *     cookie.setSecure(true);    // Solo se envía por HTTPS
     *     cookie.setMaxAge(3600);    // Define un tiempo de expiración (seguridad adicional)
     *
     *     response.addCookie(cookie);
     * }
     */
}
