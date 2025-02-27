package com.example.coverage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de ejemplo para la clase Calculator.
 * NOTA: No cubrimos todos los métodos a propósito
 * para ilustrar un ejemplo de cobertura parcial.
 */
public class CalculatorTest {

    @Test
    public void testAdd() {
        Calculator calc = new Calculator();
        int result = calc.add(2, 3);
        Assertions.assertEquals(5, result, "2 + 3 debería ser 5");
    }

    @Test
    public void testSubtract() {
        Calculator calc = new Calculator();
        int result = calc.subtract(5, 3);
        Assertions.assertEquals(2, result, "5 - 3 debería ser 2");
    }

    // EJEMPLO: No se prueban multiply ni divide, para demostrar que no se alcanza el 100% de cobertura.

    @Test
    public void testIsEven_True() {
        Calculator calc = new Calculator();
        boolean result = calc.isEven(4);
        Assertions.assertTrue(result, "4 es un número par");
    }

    @Test
    public void testIsEven_False() {
        Calculator calc = new Calculator();
        boolean result = calc.isEven(5);
        Assertions.assertFalse(result, "5 es un número impar");
    }
}
