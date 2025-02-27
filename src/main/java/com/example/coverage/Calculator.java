package com.example.coverage;

/**
 * Clase de ejemplo con métodos básicos de cálculo.
 */
public class Calculator {

    /**
     * Suma dos números enteros.
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * Resta dos números enteros.
     */
    public int subtract(int a, int b) {
        return a - b;
    }

    /**
     * Multiplica dos números enteros.
     */
    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * Divide dos números enteros.
     * Lanza ArithmeticException si b es 0.
     */
    public int divide(int a, int b) {
        return a / b;
    }

    /**
     * Retorna true si el número es par, false en caso contrario.
     */
    public boolean isEven(int number) {
        return number % 2 == 0;
    }
}
