package com.mycompany.minipc.isa;

import com.mycompany.minipc.util.BinUtil;

/**
 * Una instruccion ya traducida, lista para guardarse en memoria.
 *
 * El objeto es inmutable. Conserva ademas el texto original y el numero
 * de linea del archivo fuente, que la interfaz necesita para mostrar la
 * tabla de instrucciones y para resaltar la que apunta el PC.
 *
 * Formato de la palabra, de 16 bits:
 *
 *   bits 15-12: opcode
 *   bits 11-8 : registro
 *   bits 7-0  : operando en signo-magnitud
 */
public final class Instruccion {

    private final OpCode opcode;
    private final RegistroID registro;
    private final int operando;
    private final String textoFuente;
    private final int numeroLinea;

    /**
     * @param opcode      operacion a ejecutar
     * @param registro    registro sobre el que opera
     * @param operando    valor inmediato, o cero si la operacion no lo usa
     * @param textoFuente linea original del archivo, tal como venia
     * @param numeroLinea numero de linea dentro del archivo, desde 1
     */
    public Instruccion(OpCode opcode, RegistroID registro, int operando,
            String textoFuente, int numeroLinea) {
        if (opcode == null || registro == null) {
            throw new IllegalArgumentException("La operacion y el registro son obligatorios");
        }
        if (!opcode.requiereInmediato() && operando != 0) {
            throw new IllegalArgumentException(
                    "La operacion " + opcode + " no admite operando inmediato");
        }
        // Valida de una vez que el operando quepa en los ocho bits del formato.
        BinUtil.aSignoMagnitud(operando);

        this.opcode = opcode;
        this.registro = registro;
        this.operando = operando;
        this.textoFuente = textoFuente;
        this.numeroLinea = numeroLinea;
    }

    public OpCode getOpcode() {
        return opcode;
    }

    public RegistroID getRegistro() {
        return registro;
    }

    public int getOperando() {
        return operando;
    }

    public String getTextoFuente() {
        return textoFuente;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    /**
     * Arma la palabra de 16 bits que se guarda en la celda de memoria.
     *
     * @return la instruccion codificada
     */
    public int aPalabra() {
        return (opcode.getCodigo() << 12)
                | (registro.getCodigo() << 8)
                | BinUtil.aSignoMagnitud(operando);
    }

    /**
     * Representa la palabra en binario, agrupada como la muestra el
     * enunciado: opcode, registro y operando.
     *
     * @return por ejemplo "0011 0001 00000101"
     */
    public String aBinarioFormateado() {
        return BinUtil.aBinarioPalabra(aPalabra());
    }

    @Override
    public String toString() {
        return textoFuente != null ? textoFuente : (opcode + " " + registro);
    }
}
