package com.mycompany.minipc.isa;

import com.mycompany.minipc.util.BinUtil;

/**
 * Nombre: Instruccion
 * Entradas: la operacion, el registro, el operando, el texto original y el
 *           numero de linea del archivo
 * Salidas: no aplica
 * Restricciones: es inmutable y final; todos sus campos se fijan al
 *                construirla y no hay forma de alterarlos despues
 * Descripcion: una instruccion ya traducida, lista para guardarse en memoria.
 *              Conserva ademas el texto original y el numero de linea del
 *              archivo fuente, que la interfaz necesita para llenar la tabla
 *              de instrucciones y para resaltar la que apunta el PC. El
 *              formato de la palabra es de dieciseis bits: bits 15 a 12 el
 *              opcode, bits 11 a 8 el registro y bits 7 a 0 el operando en
 *              signo-magnitud.
 */
public final class Instruccion {

    private final OpCode opcode;
    private final RegistroID registro;
    private final int operando;
    private final String textoFuente;
    private final int numeroLinea;

    /**
     * Nombre: Instruccion
     * Entradas: opcode, operacion a ejecutar; registro, registro sobre el que
     *           opera; operando, valor inmediato o cero si no lo usa;
     *           textoFuente, linea original tal como venia; numeroLinea,
     *           posicion dentro del archivo contando desde uno
     * Salidas: la instruccion construida
     * Restricciones: opcode y registro no pueden ser nulos; si la operacion
     *                no admite inmediato el operando debe ser cero; el
     *                operando debe caber en ocho bits de signo-magnitud. En
     *                los dos primeros casos lanza IllegalArgumentException y
     *                en el tercero DesbordamientoException
     * Descripcion: valida de una vez todo lo que podria hacer invalida a la
     *              instruccion, de modo que si el objeto existe se garantiza
     *              que su codificacion es correcta.
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

    /**
     * Nombre: getOpcode
     * Entradas: ninguna
     * Salidas: la operacion que ejecuta la instruccion
     * Restricciones: ninguna
     * Descripcion: acceso de solo lectura al campo correspondiente.
     */
    public OpCode getOpcode() {
        return opcode;
    }

    /**
     * Nombre: getRegistro
     * Entradas: ninguna
     * Salidas: el registro sobre el que opera la instruccion
     * Restricciones: ninguna
     * Descripcion: acceso de solo lectura al campo correspondiente.
     */
    public RegistroID getRegistro() {
        return registro;
    }

    /**
     * Nombre: getOperando
     * Entradas: ninguna
     * Salidas: el valor inmediato, o cero si la operacion no lo usa
     * Restricciones: ninguna
     * Descripcion: acceso de solo lectura al campo correspondiente.
     */
    public int getOperando() {
        return operando;
    }

    /**
     * Nombre: getTextoFuente
     * Entradas: ninguna
     * Salidas: la linea del archivo tal como fue escrita
     * Restricciones: ninguna
     * Descripcion: se muestra en la tabla de instrucciones y se guarda como
     *              etiqueta de la celda de memoria, para que el usuario vea
     *              su propio codigo y no una reconstruccion.
     */
    public String getTextoFuente() {
        return textoFuente;
    }

    /**
     * Nombre: getNumeroLinea
     * Entradas: ninguna
     * Salidas: la posicion de la instruccion dentro del archivo, desde uno
     * Restricciones: ninguna
     * Descripcion: permite que los mensajes de error apunten a la linea real
     *              del archivo, aunque haya comentarios y lineas vacias que
     *              no ocupan memoria.
     */
    public int getNumeroLinea() {
        return numeroLinea;
    }

    /**
     * Nombre: aPalabra
     * Entradas: ninguna
     * Salidas: la instruccion codificada en dieciseis bits
     * Restricciones: ninguna, el operando ya fue validado al construir
     * Descripcion: arma la palabra que se guarda en la celda de memoria,
     *              corriendo el opcode doce posiciones, el registro ocho, y
     *              dejando el operando en el byte bajo.
     */
    public int aPalabra() {
        return (opcode.getCodigo() << 12)
                | (registro.getCodigo() << 8)
                | BinUtil.aSignoMagnitud(operando);
    }

    /**
     * Nombre: aBinarioFormateado
     * Entradas: ninguna
     * Salidas: la palabra en binario agrupada como en el enunciado
     * Restricciones: ninguna
     * Descripcion: devuelve por ejemplo "0011 0001 00000101" para MOV AX, 5.
     *              Es lo que se muestra en la columna Binario de la tabla de
     *              instrucciones.
     */
    public String aBinarioFormateado() {
        return BinUtil.aBinarioPalabra(aPalabra());
    }

    /**
     * Nombre: toString
     * Entradas: ninguna
     * Salidas: representacion legible de la instruccion
     * Restricciones: ninguna
     * Descripcion: devuelve el texto original si existe; si no, reconstruye
     *              una descripcion a partir de la operacion y el registro.
     */
    @Override
    public String toString() {
        return textoFuente != null ? textoFuente : (opcode + " " + registro);
    }
}
