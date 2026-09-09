package com.mycompany.minipc.util;

import com.mycompany.minipc.excepciones.DesbordamientoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la conversion a signo-magnitud.
 *
 * El caso de referencia es MOV BX, -8, que segun el enunciado debe
 * codificarse como 10001000 y no como el complemento a dos.
 */
class BinUtilTest {

    @Test
    @DisplayName("El -8 del enunciado se codifica como 10001000")
    void codificaElMenosOchoDelEnunciado() {
        assertEquals(0b10001000, BinUtil.aSignoMagnitud(-8));
        assertEquals("10001000", BinUtil.aBinarioEntero(-8));
    }

    @Test
    @DisplayName("Los positivos del enunciado se codifican sin bit de signo")
    void codificaLosPositivosDelEnunciado() {
        assertEquals(0b00000101, BinUtil.aSignoMagnitud(5));
        assertEquals(0b00000011, BinUtil.aSignoMagnitud(3));
        assertEquals("00000101", BinUtil.aBinarioEntero(5));
    }

    @Test
    @DisplayName("El cero se normaliza siempre a 00000000")
    void normalizaElCero() {
        assertEquals(0, BinUtil.aSignoMagnitud(0));
        assertEquals("00000000", BinUtil.aBinarioEntero(0));
    }

    @Test
    @DisplayName("El cero negativo 10000000 se interpreta como cero")
    void interpretaElCeroNegativo() {
        assertEquals(0, BinUtil.aEntero(0b10000000));
    }

    @Test
    @DisplayName("La ida y vuelta conserva el valor en todo el rango")
    void idaYVueltaEnTodoElRango() {
        for (int valor = BinUtil.VALOR_MINIMO; valor <= BinUtil.VALOR_MAXIMO; valor++) {
            int codificado = BinUtil.aSignoMagnitud(valor);
            assertEquals(valor, BinUtil.aEntero(codificado),
                    "Fallo la ida y vuelta para " + valor);
        }
    }

    @Test
    @DisplayName("Los extremos del rango se aceptan")
    void aceptaLosExtremos() {
        assertEquals(0b01111111, BinUtil.aSignoMagnitud(127));
        assertEquals(0b11111111, BinUtil.aSignoMagnitud(-127));
        assertTrue(BinUtil.esRepresentable(127));
        assertTrue(BinUtil.esRepresentable(-127));
    }

    @Test
    @DisplayName("Los valores fuera de rango se rechazan")
    void rechazaFueraDeRango() {
        assertFalse(BinUtil.esRepresentable(128));
        assertFalse(BinUtil.esRepresentable(-128));
        assertThrows(DesbordamientoException.class, () -> BinUtil.aSignoMagnitud(128));
        assertThrows(DesbordamientoException.class, () -> BinUtil.aSignoMagnitud(-128));
        assertThrows(DesbordamientoException.class, () -> BinUtil.aSignoMagnitud(300));
    }

    @Test
    @DisplayName("aBinario rellena con ceros a la izquierda")
    void rellenaConCeros() {
        assertEquals("00000101", BinUtil.aBinario(5, 8));
        assertEquals("0011", BinUtil.aBinario(0b0011, 4));
        assertEquals("0000000000000101", BinUtil.aBinario(5, 16));
    }

    @Test
    @DisplayName("aBinario rechaza cantidades de bits invalidas")
    void rechazaBitsInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> BinUtil.aBinario(5, 0));
        assertThrows(IllegalArgumentException.class, () -> BinUtil.aBinario(5, 33));
    }

    @Test
    @DisplayName("La palabra de 16 bits se agrupa como opcode, registro y operando")
    void agrupaLaPalabraDeInstruccion() {
        assertEquals("0011 0001 00000101", BinUtil.aBinarioPalabra(0b0011000100000101));
        assertEquals("0011 0010 10001000", BinUtil.aBinarioPalabra(0b0011001010001000));
        assertEquals("0101 0010 00000000", BinUtil.aBinarioPalabra(0b0101001000000000));
    }
}
