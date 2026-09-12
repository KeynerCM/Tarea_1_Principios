package com.mycompany.minipc.core;

import com.mycompany.minipc.excepciones.DesbordamientoException;
import com.mycompany.minipc.isa.RegistroID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de los registros y de la celda de memoria.
 */
class BancoRegistrosTest {

    private BancoRegistros banco;

    @BeforeEach
    void prepararBanco() {
        banco = new BancoRegistros();
    }

    @Test
    @DisplayName("Los cuatro registros arrancan en cero")
    void arrancanEnCero() {
        for (RegistroID id : RegistroID.values()) {
            assertEquals(0, banco.leer(id));
        }
        assertEquals(4, banco.todos().size());
    }

    @Test
    @DisplayName("Escribir y leer devuelve el mismo valor")
    void escrituraYLectura() {
        banco.escribir(RegistroID.AX, 5);
        banco.escribir(RegistroID.BX, -8);
        assertEquals(5, banco.leer(RegistroID.AX));
        assertEquals(-8, banco.leer(RegistroID.BX));
        assertEquals(0, banco.leer(RegistroID.CX));
    }

    @Test
    @DisplayName("El registro rechaza valores fuera del formato de 8 bits")
    void rechazaFueraDeRango() {
        assertThrows(DesbordamientoException.class,
                () -> banco.escribir(RegistroID.AX, 128));
        assertThrows(DesbordamientoException.class,
                () -> banco.escribir(RegistroID.AX, -128));
        assertEquals(0, banco.leer(RegistroID.AX), "El valor no debio cambiar");
    }

    @Test
    @DisplayName("El binario del registro usa signo-magnitud")
    void binarioEnSignoMagnitud() {
        banco.escribir(RegistroID.BX, -8);
        assertEquals("10001000", banco.obtener(RegistroID.BX).getBinario());
        banco.escribir(RegistroID.AX, 5);
        assertEquals("00000101", banco.obtener(RegistroID.AX).getBinario());
    }

    @Test
    @DisplayName("El reset pone todo en cero")
    void resetLimpiaTodo() {
        banco.escribir(RegistroID.AX, 100);
        banco.escribir(RegistroID.DX, -100);
        banco.reset();
        for (RegistroID id : RegistroID.values()) {
            assertEquals(0, banco.leer(id));
        }
    }

    @Test
    @DisplayName("El recorrido conserva el orden AX, BX, CX, DX")
    void ordenDeRecorrido() {
        List<RegistroID> orden = new ArrayList<>();
        for (Registro registro : banco.todos()) {
            orden.add(registro.getId());
        }
        assertEquals(List.of(RegistroID.AX, RegistroID.BX,
                RegistroID.CX, RegistroID.DX), orden);
    }

    @Test
    @DisplayName("La instantanea es una copia independiente")
    void instantaneaIndependiente() {
        banco.escribir(RegistroID.AX, 7);
        Map<RegistroID, Integer> copia = banco.instantanea();
        assertEquals(7, copia.get(RegistroID.AX));

        banco.escribir(RegistroID.AX, 99);
        assertEquals(7, copia.get(RegistroID.AX), "La copia no debio seguir al banco");
        assertEquals(99, banco.leer(RegistroID.AX));
    }

    @Test
    @DisplayName("La celda de memoria arranca libre y vacia")
    void celdaArrancaLibre() {
        CeldaMemoria celda = new CeldaMemoria();
        assertTrue(celda.estaLibre());
        assertEquals(CeldaMemoria.Tipo.LIBRE, celda.getTipo());
        assertEquals(0, celda.getPalabra());
        assertEquals("", celda.getBinario());
    }

    @Test
    @DisplayName("La celda guarda la palabra, el tipo y la etiqueta")
    void celdaGuardaContenido() {
        CeldaMemoria celda = new CeldaMemoria();
        celda.escribir(0b0011000100000101, CeldaMemoria.Tipo.INSTRUCCION, "MOV AX, 5");

        assertFalse(celda.estaLibre());
        assertEquals(CeldaMemoria.Tipo.INSTRUCCION, celda.getTipo());
        assertEquals("MOV AX, 5", celda.getEtiqueta());
        assertEquals("0011 0001 00000101", celda.getBinario());
    }

    @Test
    @DisplayName("La celda se puede volver a dejar libre")
    void celdaSeLimpia() {
        CeldaMemoria celda = new CeldaMemoria();
        celda.escribir(0b0011000100000101, CeldaMemoria.Tipo.INSTRUCCION, "MOV AX, 5");
        celda.limpiar();
        assertTrue(celda.estaLibre());
        assertEquals(0, celda.getPalabra());
        assertEquals("", celda.getEtiqueta());
    }

    @Test
    @DisplayName("Los estados finales se distinguen de los demas")
    void estadosFinales() {
        assertTrue(EstadoProceso.TERMINADO.esFinal());
        assertTrue(EstadoProceso.BLOQUEADO_ERROR.esFinal());
        assertFalse(EstadoProceso.NUEVO.esFinal());
        assertFalse(EstadoProceso.LISTO.esFinal());
        assertFalse(EstadoProceso.EJECUCION.esFinal());
    }
}
