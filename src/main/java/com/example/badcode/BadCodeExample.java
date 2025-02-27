package com.example.badcode;

public class BadCodeExample {

    public static int globalCount = 0; // Code smell: campo público estático mutable
    private String unusedField;        // Code smell: campo nunca usado

    public BadCodeExample(String name) {
        // Code smell: no se usa el parámetro ni se inicializa el campo
    }

    public static void main(String[] args) {

        // Code smell/bug: bucle infinito potencial (i-- en lugar de i++)
        for (int i = 0; i < 10; i--) {
            globalCount++;
            if(globalCount > 5){
                // Mala práctica: lógica de corte forzada
                break;
            }
        }

        // Code smell/bug: posible NullPointerException
        String possibleNull = null;
        System.out.println(possibleNull.toLowerCase());

        // Code smell: posible inyección SQL al concatenar parámetros directamente
        doDatabaseQuery("someParameter' OR '1'='1");

        // Code smell: uso de método que no hace nada productivo
        unusedMethod();

        // Code smell: System.exit interrumpe la JVM abruptamente
        System.exit(0);
    }

    public static void doDatabaseQuery(String param) {
        // Code smell: concatenación directa de parámetros => inyección SQL
        String sql = "SELECT * FROM my_table WHERE column = '" + param + "'";
        System.out.println("Executing query: " + sql);
    }

    private static void unusedMethod() {
        // Code smell: variable local sin usar realmente
        int a = 42;

        // Code smell/bug: posible ArithmeticException por división entre cero
        int result = a / 0;
        System.out.println("Result: " + result);

        // Code smell: bloque catch vacío (silencia excepciones)
        try {
            throw new Exception("Test exception");
        } catch(Exception e) {
            // Intencionadamente vacío
        }
    }
}
