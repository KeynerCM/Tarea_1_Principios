package com.mycompany.minipc.isa;

/**
 * Nombre: RegistroID
 * Entradas: no aplica, es una enumeracion de valores fijos
 * Salidas: no aplica
 * Restricciones: los codigos son los de la lamina 7 del enunciado y no deben
 *                cambiarse; ninguno vale cero, de modo que un campo de
 *                registro en cero indica una palabra invalida
 * Descripcion: identidad de los cuatro registros de proposito general del
 *              Mini PC, junto con el codigo de cuatro bits que los ubica en
 *              la palabra de instruccion. Los nombres AX, BX, CX y DX estan
 *              tomados del x86, pero aqui son simples identificadores sin
 *              relacion con los registros reales del procesador.
 */
public enum RegistroID {

    AX(0b0001),
    BX(0b0010),
    CX(0b0011),
    DX(0b0100);

    private final int codigo;

    /**
     * Nombre: RegistroID
     * Entradas: codigo, nibble que identifica al registro
     * Salidas: la constante construida
     * Restricciones: privado, solo lo invoca la propia enumeracion
     * Descripcion: asocia a cada registro su codigo binario.
     */
    RegistroID(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Nombre: getCodigo
     * Entradas: ninguna
     * Salidas: el nibble de cuatro bits que identifica al registro
     * Restricciones: ninguna
     * Descripcion: lo usa Instruccion.aPalabra() para armar los bits 11 a 8
     *              de la palabra de memoria.
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Nombre: desdeNombre
     * Entradas: nombre, texto del registro tal como aparece en el .asm
     * Salidas: el registro correspondiente
     * Restricciones: si el nombre es nulo o no existe lanza
     *                IllegalArgumentException; el ensamblador la atrapa para
     *                convertirla en un error con numero de linea
     * Descripcion: busca el registro por su nombre sin distinguir mayusculas
     *              y descartando espacios sobrantes.
     */
    public static RegistroID desdeNombre(String nombre) {
        if (nombre != null) {
            String buscado = nombre.trim().toUpperCase();
            for (RegistroID id : values()) {
                if (id.name().equals(buscado)) {
                    return id;
                }
            }
        }
        throw new IllegalArgumentException("Registro inexistente: " + nombre);
    }

    /**
     * Nombre: desdeCodigo
     * Entradas: nibble, codigo de cuatro bits leido de la palabra en memoria
     * Salidas: el registro correspondiente
     * Restricciones: si el codigo no corresponde a ninguno lanza
     *                IllegalArgumentException
     * Descripcion: operacion inversa de getCodigo. La usa el procesador en la
     *              etapa de decodificacion, tras extraer los bits 11 a 8 del
     *              registro de instruccion.
     */
    public static RegistroID desdeCodigo(int nibble) {
        for (RegistroID id : values()) {
            if (id.codigo == nibble) {
                return id;
            }
        }
        throw new IllegalArgumentException(
                "Codigo de registro desconocido: " + Integer.toBinaryString(nibble));
    }
}
