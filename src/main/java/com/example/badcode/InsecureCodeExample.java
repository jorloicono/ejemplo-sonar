package com.example.securityissues;

import javax.net.ssl.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.io.*;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Random;

public class InsecureCodeExample {

    // 1. Hardcoded credentials (credenciales embebidas)
    private static final String SECRET_PASSWORD = "SuperSecret123!";
    
    public static void main(String[] args) {
        
        // Ejemplo de log de información sensible
        System.out.println("Contraseña embebida: " + SECRET_PASSWORD);

        // 2. Uso inseguro de Random (no criptográficamente seguro)
        Random random = new Random();
        int insecureKey = random.nextInt();
        System.out.println("Llave generada (insegura): " + insecureKey);

        // 3. Conexión SSL sin validación de certificados
        insecureSslConnection();

        // 4. Inyección de comandos (Command Injection)
        //    Simulamos que "args[0]" proviene de una entrada de usuario
        if (args.length > 0) {
            runCommand(args[0]);
        }

        // 5. Path Traversal (sin sanitizar el nombre de archivo)
        //    Simulamos que "args[1]" podría contener ../ o rutas peligrosas
        if (args.length > 1) {
            pathTraversal(args[1]);
        }

        // 6. LDAP Injection (filtros LDAP inseguros)
        if (args.length > 2) {
            ldapSearch(args[2]);
        }
    }

    /**
     * Conexión SSL con un TrustManager que acepta cualquier certificado,
     * sin validación (inseguro frente a ataques MITM).
     */
    public static void insecureSslConnection() {
        try {
            // Crea un trust manager que no valida cadenas de certificados
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Ignora la verificación del nombre del host
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            // Ahora hace una petición insegura a un sitio HTTPS
            URL url = new URL("https://example.com/");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibido: " + inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inyección de comandos: se ejecuta directamente el comando que llega como parámetro.
     * Un atacante podría enviar algo como "rm -rf /" en sistemas Unix o comandos maliciosos en Windows.
     */
    public static void runCommand(String command) {
        try {
            Runtime.getRuntime().exec(command);
            System.out.println("Comando ejecutado: " + command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Path Traversal: construimos la ruta de archivo con la entrada del usuario
     * y leemos su contenido sin validación ni sanitización.
     */
    public static void pathTraversal(String filename) {
        File file = new File("/var/data/" + filename);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Contenido: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * LDAP Injection: concatenación directa de la entrada del usuario en el filtro LDAP.
     * Un atacante podría manipular el filtro y obtener información adicional o causar errores.
     */
    public static void ldapSearch(String userFilter) {
        try {
            Hashtable<String,String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://localhost:389");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=example,dc=com");
            env.put(Context.SECURITY_CREDENTIALS, "adminPassword");

            DirContext ctx = new InitialDirContext(env);

            // Filtro inseguro: "(uid=" + userFilter + ")"
            String searchFilter = "(uid=" + userFilter + ")";
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<?> results = ctx.search("dc=example,dc=com", searchFilter, controls);

            while (results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                System.out.println("Resultado LDAP: " + sr.getNameInNamespace());
            }

            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
