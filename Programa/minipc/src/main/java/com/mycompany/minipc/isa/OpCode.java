package com.mycompany.minipc.isa;

/**
 * Juego de instrucciones del Mini PC.
 *
 * El enunciado lista los opcodes con tres bits (001 LOAD, 010 STORE,
 * 011 MOV, 100 SUB, 101 ADD) pero los ejemplos binarios los muestran con
 * cuatro (0001, 0010, 0011, 0100, 0101). Son el mismo valor con un cero
 * a la izquierda: se usa la version de cuatro bits, que es la consistente
 * con la figura 1.3d de Stallings y con las laminas del propio enunciado.
 */
public enum OpCode {

    /** Carga un valor inmediato en un registro. Rx recibe el operando. */
    MOV(0b0011, true),

    /** Copia el contenido de un registro al acumulador. AC recibe Rx. */
    LOAD(0b0001, false),

    /** Copia el acumulador a un registro. Rx recibe AC. */
    STORE(0b0010, false),

    /** Suma un registro al acumulador. AC recibe AC mas Rx. */
    ADD(0b0101, false),

    /** Resta un registro del acumulador. AC recibe AC menos Rx. */
    SUB(0b0100, false);

    private final int codigo;
    private final boolean requiereInmediato;

    OpCode(int codigo, boolean requiereInmediato) {
        this.codigo = codigo;
        this.requiereInmediato = requiereInmediato;
    }

    /**
     * @return el nibble de cuatro bits que identifica la operacion
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Indica si la instruccion lleva un valor inmediato en el operando.
     * Solo MOV lo lleva; el resto operan unicamente sobre registros y
     * dejan el operando en cero.
     *
     * @return true si la sintaxis exige un tercer token con el valor
     */
    public boolean requiereInmediato() {
        return requiereInmediato;
    }

    /**
     * Busca una operacion por su mnemonico, sin distinguir mayusculas.
     *
     * @param mnemonico texto de la operacion, por ejemplo "mov"
     * @return la operacion correspondiente
     * @throws IllegalArgumentException si el mnemonico no existe
     */
    public static OpCode desdeMnemonico(String mnemonico) {
        if (mnemonico != null) {
            String buscado = mnemonico.trim().toUpperCase();
            for (OpCode op : values()) {
                if (op.name().equals(buscado)) {
                    return op;
                }
            }
        }
        throw new IllegalArgumentException("Operacion desconocida: " + mnemonico);
    }

    /**
     * Busca una operacion por su codigo binario. Lo usa el procesador al
     * decodificar la palabra leida de memoria.
     *
     * @param nibble codigo de cuatro bits
     * @return la operacion correspondiente
     * @throws IllegalArgumentException si el codigo no corresponde a ninguna
     */
    public static OpCode desdeCodigo(int nibble) {
        for (OpCode op : values()) {
            if (op.codigo == nibble) {
                return op;
            }
        }
        throw new IllegalArgumentException(
                "Codigo de operacion desconocido: " + Integer.toBinaryString(nibble));
    }
}
