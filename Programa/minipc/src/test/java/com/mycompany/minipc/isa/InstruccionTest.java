package com.mycompany.minipc.isa;

import com.mycompany.minipc.excepciones.DesbordamientoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la codificacion a 16 bits.
 *
 * El caso de referencia es la tabla de la lamina 7 del enunciado, que da
 * el binario exacto de las siete instrucciones del programa de ejemplo.
 */
class InstruccionTest {

    private static Instruccion instr(OpCode op, RegistroID reg, int operando, String texto) {
        return new Instruccion(op, reg, operando, texto, 1);
    }

    @Test
    @DisplayName("Las siete lineas del enunciado producen el binario esperado")
    void codificaLaTablaDelEnunciado() {
        assertEquals("0011 0001 00000101",
                instr(OpCode.MOV, RegistroID.AX, 5, "MOV AX, 5").aBinarioFormateado());
        assertEquals("0011 0010 00000011",
                instr(OpCode.MOV, RegistroID.BX, 3, "MOV BX, 3").aBinarioFormateado());
        assertEquals("0001 0001 00000000",
                instr(OpCode.LOAD, RegistroID.AX, 0, "LOAD AX").aBinarioFormateado());
        assertEquals("0101 0010 00000000",
                instr(OpCode.ADD, RegistroID.BX, 0, "ADD BX").aBinarioFormateado());
        assertEquals("0100 0001 00000000",
                instr(OpCode.SUB, RegistroID.AX, 0, "SUB AX").aBinarioFormateado());
        assertEquals("0010 0001 00000000",
                instr(OpCode.STORE, RegistroID.AX, 0, "STORE AX").aBinarioFormateado());
        assertEquals("0011 0010 10001000",
                instr(OpCode.MOV, RegistroID.BX, -8, "MOV BX, -8").aBinarioFormateado());
    }

    @Test
    @DisplayName("La palabra ubica opcode, registro y operando en su lugar")
    void ubicaLosTresCampos() {
        int palabra = instr(OpCode.MOV, RegistroID.AX, 5, "MOV AX, 5").aPalabra();
        assertEquals(0b0011, palabra >>> 12);
        assertEquals(0b0001, (palabra >>> 8) & 0xF);
        assertEquals(0b00000101, palabra & 0xFF);
        assertEquals(0b0011000100000101, palabra);
    }

    @Test
    @DisplayName("La instruccion conserva el texto y la linea de origen")
    void conservaElOrigen() {
        Instruccion i = new Instruccion(OpCode.MOV, RegistroID.CX, 100, "MOV CX, 100", 7);
        assertEquals("MOV CX, 100", i.getTextoFuente());
        assertEquals(7, i.getNumeroLinea());
        assertEquals(100, i.getOperando());
        assertEquals("MOV CX, 100", i.toString());
    }

    @Test
    @DisplayName("Un operando fuera de rango se rechaza al construir")
    void rechazaOperandoFueraDeRango() {
        assertThrows(DesbordamientoException.class,
                () -> instr(OpCode.MOV, RegistroID.BX, 300, "MOV BX, 300"));
        assertThrows(DesbordamientoException.class,
                () -> instr(OpCode.MOV, RegistroID.BX, -128, "MOV BX, -128"));
    }

    @Test
    @DisplayName("Solo MOV admite operando inmediato")
    void soloMovAdmiteInmediato() {
        assertTrue(OpCode.MOV.requiereInmediato());
        assertFalse(OpCode.LOAD.requiereInmediato());
        assertFalse(OpCode.STORE.requiereInmediato());
        assertFalse(OpCode.ADD.requiereInmediato());
        assertFalse(OpCode.SUB.requiereInmediato());
        assertThrows(IllegalArgumentException.class,
                () -> instr(OpCode.ADD, RegistroID.BX, 5, "ADD BX, 5"));
    }

    @Test
    @DisplayName("Los opcodes valen lo que dice el enunciado")
    void codigosDeOperacion() {
        assertEquals(0b0001, OpCode.LOAD.getCodigo());
        assertEquals(0b0010, OpCode.STORE.getCodigo());
        assertEquals(0b0011, OpCode.MOV.getCodigo());
        assertEquals(0b0100, OpCode.SUB.getCodigo());
        assertEquals(0b0101, OpCode.ADD.getCodigo());
    }

    @Test
    @DisplayName("Los registros valen lo que dice el enunciado")
    void codigosDeRegistro() {
        assertEquals(0b0001, RegistroID.AX.getCodigo());
        assertEquals(0b0010, RegistroID.BX.getCodigo());
        assertEquals(0b0011, RegistroID.CX.getCodigo());
        assertEquals(0b0100, RegistroID.DX.getCodigo());
    }

    @Test
    @DisplayName("Las busquedas por nombre ignoran mayusculas y espacios")
    void busquedaPorNombre() {
        assertEquals(OpCode.MOV, OpCode.desdeMnemonico("mov"));
        assertEquals(OpCode.ADD, OpCode.desdeMnemonico("  Add  "));
        assertEquals(RegistroID.AX, RegistroID.desdeNombre("ax"));
        assertEquals(RegistroID.DX, RegistroID.desdeNombre(" Dx "));
    }

    @Test
    @DisplayName("Las busquedas rechazan mnemonicos y registros inexistentes")
    void busquedaRechazaDesconocidos() {
        assertThrows(IllegalArgumentException.class, () -> OpCode.desdeMnemonico("JUMP"));
        assertThrows(IllegalArgumentException.class, () -> OpCode.desdeMnemonico(null));
        assertThrows(IllegalArgumentException.class, () -> RegistroID.desdeNombre("EX"));
        assertThrows(IllegalArgumentException.class, () -> RegistroID.desdeNombre(null));
    }

    @Test
    @DisplayName("La busqueda por codigo binario reconstruye la operacion y el registro")
    void busquedaPorCodigo() {
        for (OpCode op : OpCode.values()) {
            assertEquals(op, OpCode.desdeCodigo(op.getCodigo()));
        }
        for (RegistroID id : RegistroID.values()) {
            assertEquals(id, RegistroID.desdeCodigo(id.getCodigo()));
        }
        assertThrows(IllegalArgumentException.class, () -> OpCode.desdeCodigo(0b1111));
        assertThrows(IllegalArgumentException.class, () -> RegistroID.desdeCodigo(0b0000));
    }
}
