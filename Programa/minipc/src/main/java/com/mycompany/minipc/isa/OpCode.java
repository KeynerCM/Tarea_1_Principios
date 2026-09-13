package com.mycompany.minipc.isa;

/**
 * Nombre: OpCode
 * Entradas: no aplica, es una enumeracion de valores fijos
 * Salidas: no aplica
 * Restricciones: los codigos son los del enunciado y no deben cambiarse, ya
 *                que determinan el binario que se guarda en memoria
 * Descripcion: juego de instrucciones del Mini PC. El enunciado lista los
 *              opcodes con tres bits (001 LOAD, 010 STORE, 011 MOV, 100 SUB,
 *              101 ADD) pero los ejemplos binarios los muestran con cuatro
 *              (0001, 0010, 0011, 0100, 0101). Son el mismo valor con un
 *              cero a la izquierda: se usa la version de cuatro bits, que es
 *              la consistente con la figura 1.3d de Stallings y con las
 *              laminas del propio enunciado.
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

    /**
     * Nombre: OpCode
     * Entradas: codigo, nibble que identifica la operacion;
     *           requiereInmediato, si la sintaxis exige un valor literal
     * Salidas: la constante construida
     * Restricciones: privado, solo lo invoca la propia enumeracion
     * Descripcion: asocia a cada operacion su codigo binario y si lleva o no
     *              un operando inmediato.
     */
    OpCode(int codigo, boolean requiereInmediato) {
        this.codigo = codigo;
        this.requiereInmediato = requiereInmediato;
    }

    /**
     * Nombre: getCodigo
     * Entradas: ninguna
     * Salidas: el nibble de cuatro bits que identifica la operacion
     * Restricciones: ninguna
     * Descripcion: lo usa Instruccion.aPalabra() para armar los bits 15 a 12
     *              de la palabra de memoria.
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Nombre: requiereInmediato
     * Entradas: ninguna
     * Salidas: true si la instruccion lleva un valor inmediato
     * Restricciones: ninguna
     * Descripcion: solo MOV lo lleva; el resto operan unicamente sobre
     *              registros y dejan el campo de operando en cero. El
     *              ensamblador lo consulta para saber cuantos tokens esperar.
     */
    public boolean requiereInmediato() {
        return requiereInmediato;
    }

    /**
     * Nombre: desdeMnemonico
     * Entradas: mnemonico, texto de la operacion tal como aparece en el .asm
     * Salidas: la operacion correspondiente
     * Restricciones: si el mnemonico es nulo o no existe lanza
     *                IllegalArgumentException; el ensamblador la atrapa para
     *                convertirla en un error con numero de linea
     * Descripcion: busca la operacion por su nombre sin distinguir mayusculas
     *              y descartando espacios sobrantes.
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
     * Nombre: desdeCodigo
     * Entradas: nibble, codigo de cuatro bits leido de la palabra en memoria
     * Salidas: la operacion correspondiente
     * Restricciones: si el codigo no corresponde a ninguna operacion lanza
     *                IllegalArgumentException
     * Descripcion: operacion inversa de getCodigo. La usa el procesador en la
     *              etapa de decodificacion, tras extraer los bits 15 a 12 del
     *              registro de instruccion.
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
