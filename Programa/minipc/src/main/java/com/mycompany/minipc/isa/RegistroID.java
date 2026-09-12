package com.mycompany.minipc.isa;

/**
 * Los cuatro registros de proposito general del Mini PC, con el codigo
 * de cuatro bits que los identifica dentro de la palabra de instruccion.
 */
public enum RegistroID {

    AX(0b0001),
    BX(0b0010),
    CX(0b0011),
    DX(0b0100);

    private final int codigo;

    RegistroID(int codigo) {
        this.codigo = codigo;
    }

    /**
     * @return el nibble de cuatro bits que identifica al registro
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Busca un registro por su nombre, sin distinguir mayusculas.
     *
     * @param nombre nombre del registro, por ejemplo "ax"
     * @return el registro correspondiente
     * @throws IllegalArgumentException si el nombre no existe
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
     * Busca un registro por su codigo binario. Lo usa el procesador al
     * decodificar la palabra leida de memoria.
     *
     * @param nibble codigo de cuatro bits
     * @return el registro correspondiente
     * @throws IllegalArgumentException si el codigo no corresponde a ninguno
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
