package com.mycompany.minipc.util;

import com.mycompany.minipc.excepciones.DesbordamientoException;

/**
 * Nombre: BinUtil
 * Entradas: no aplica, la clase no se instancia ni guarda estado
 * Salidas: no aplica
 * Restricciones: es final y su constructor es privado, de modo que solo
 *                se usa a traves de sus metodos estaticos
 * Descripcion: utilidades de conversion entre enteros de Java y la
 *              representacion binaria del Mini PC. El formato de entero del
 *              enunciado es de ocho bits en signo-magnitud: el bit 7 lleva
 *              el signo (0 positivo, 1 negativo) y los bits 6 a 0 la
 *              magnitud. NO es complemento a dos, por lo que
 *              Integer.toBinaryString produce un resultado incorrecto para
 *              esta maquina y la conversion se hace a mano.
 */
public final class BinUtil {

    /** Mayor valor representable en ocho bits de signo-magnitud. */
    public static final int VALOR_MAXIMO = 127;

    /** Menor valor representable en ocho bits de signo-magnitud. */
    public static final int VALOR_MINIMO = -127;

    /** Mascara de la magnitud: los siete bits bajos. */
    private static final int MASCARA_MAGNITUD = 0x7F;

    /** Mascara del bit de signo: el bit 7. */
    private static final int MASCARA_SIGNO = 0x80;

    /**
     * Nombre: BinUtil
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: lanza AssertionError siempre, porque la clase no debe
     *                instanciarse
     * Descripcion: constructor privado que impide crear objetos de una
     *              clase que solo contiene utilidades estaticas.
     */
    private BinUtil() {
        throw new AssertionError("BinUtil es una clase de utilidades, no se instancia");
    }

    /**
     * Nombre: esRepresentable
     * Entradas: valor, entero de Java a evaluar
     * Salidas: true si el valor cabe en el formato de ocho bits, false si no
     * Restricciones: ninguna, acepta cualquier entero
     * Descripcion: indica si un valor esta dentro del rango -127 a 127 que
     *              admite el formato de signo-magnitud de ocho bits.
     */
    public static boolean esRepresentable(int valor) {
        return valor >= VALOR_MINIMO && valor <= VALOR_MAXIMO;
    }

    /**
     * Nombre: aSignoMagnitud
     * Entradas: valor, entero de Java entre -127 y 127
     * Salidas: el patron de ocho bits en signo-magnitud, alojado en los ocho
     *          bits bajos de un int
     * Restricciones: si el valor esta fuera del rango representable lanza
     *                DesbordamientoException
     * Descripcion: convierte un entero al formato del Mini PC. La magnitud
     *              se toma del valor absoluto y el bit 7 se enciende solo si
     *              el valor es negativo. El cero siempre se normaliza a
     *              00000000: el formato admite dos ceros, pero aqui solo se
     *              genera el positivo.
     */
    public static int aSignoMagnitud(int valor) {
        if (!esRepresentable(valor)) {
            throw new DesbordamientoException(
                    "El valor " + valor + " esta fuera del rango representable ("
                    + VALOR_MINIMO + " a " + VALOR_MAXIMO + ")");
        }
        int magnitud = Math.abs(valor) & MASCARA_MAGNITUD;
        return valor < 0 ? (magnitud | MASCARA_SIGNO) : magnitud;
    }

    /**
     * Nombre: aEntero
     * Entradas: byteSignoMagnitud, patron de bits del que solo se consideran
     *           los ocho bits bajos
     * Salidas: el entero de Java equivalente, entre -127 y 127
     * Restricciones: ninguna, cualquier patron de ocho bits es valido
     * Descripcion: operacion inversa de aSignoMagnitud. Separa la magnitud
     *              del bit de signo y arma el entero. El cero negativo,
     *              10000000, se interpreta como cero.
     */
    public static int aEntero(int byteSignoMagnitud) {
        int patron = byteSignoMagnitud & 0xFF;
        int magnitud = patron & MASCARA_MAGNITUD;
        boolean negativo = (patron & MASCARA_SIGNO) != 0;
        return negativo ? -magnitud : magnitud;
    }

    /**
     * Nombre: aBinario
     * Entradas: patron, bits a representar; bits, cuantos se muestran
     * Salidas: cadena binaria de la longitud pedida, con ceros a la izquierda
     *          y sin separadores
     * Restricciones: bits debe estar entre 1 y 32; si no, lanza
     *                IllegalArgumentException
     * Descripcion: representa un patron de bits crudo, sin interpretarlo como
     *              valor con signo. Para mostrar un entero del Mini PC en su
     *              codificacion corresponde usar aBinarioEntero.
     */
    public static String aBinario(int patron, int bits) {
        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException(
                    "La cantidad de bits debe estar entre 1 y 32, se recibio " + bits);
        }
        StringBuilder sb = new StringBuilder(bits);
        for (int i = bits - 1; i >= 0; i--) {
            sb.append((patron >>> i) & 1);
        }
        return sb.toString();
    }

    /**
     * Nombre: aBinarioEntero
     * Entradas: valor, entero de Java entre -127 y 127
     * Salidas: cadena de ocho caracteres con la codificacion en signo-magnitud
     * Restricciones: si el valor esta fuera de rango lanza
     *                DesbordamientoException, heredada de aSignoMagnitud
     * Descripcion: combina la codificacion y el formateo para mostrar un
     *              entero del Mini PC tal como aparece en el enunciado. Por
     *              ejemplo, -8 se muestra como 10001000.
     */
    public static String aBinarioEntero(int valor) {
        return aBinario(aSignoMagnitud(valor), 8);
    }

    /**
     * Nombre: aBinarioPalabra
     * Entradas: palabra, instruccion codificada de dieciseis bits
     * Salidas: cadena con los tres campos separados por espacios
     * Restricciones: solo se consideran los dieciseis bits bajos del argumento
     * Descripcion: representa una palabra de instruccion con la agrupacion
     *              del enunciado, cuatro bits de opcode, cuatro de registro y
     *              ocho de operando. Por ejemplo, MOV AX, 5 se muestra como
     *              "0011 0001 00000101".
     */
    public static String aBinarioPalabra(int palabra) {
        int p = palabra & 0xFFFF;
        return aBinario(p >>> 12, 4) + " "
                + aBinario((p >>> 8) & 0xF, 4) + " "
                + aBinario(p & 0xFF, 8);
    }
}
