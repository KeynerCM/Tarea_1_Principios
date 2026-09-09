package com.mycompany.minipc.util;

import com.mycompany.minipc.excepciones.DesbordamientoException;

/**
 * Utilidades de conversion entre enteros de Java y la representacion
 * binaria que usa el Mini PC.
 *
 * El formato de entero del enunciado es de 8 bits en signo-magnitud:
 *
 *   bit 7   : signo (0 positivo, 1 negativo)
 *   bits 6-0: magnitud
 *
 * Esto NO es complemento a dos. Integer.toBinaryString(-8) devuelve
 * complemento a dos y produce un resultado incorrecto para esta maquina;
 * por eso la conversion se hace a mano.
 *
 * Clase de utilidades puras: sin estado y no instanciable.
 */
public final class BinUtil {

    /** Mayor valor representable en 8 bits de signo-magnitud. */
    public static final int VALOR_MAXIMO = 127;

    /** Menor valor representable en 8 bits de signo-magnitud. */
    public static final int VALOR_MINIMO = -127;

    /** Mascara de la magnitud: los 7 bits bajos. */
    private static final int MASCARA_MAGNITUD = 0x7F;

    /** Mascara del bit de signo: el bit 7. */
    private static final int MASCARA_SIGNO = 0x80;

    private BinUtil() {
        throw new AssertionError("BinUtil es una clase de utilidades, no se instancia");
    }

    /**
     * Indica si un valor cabe en el formato de entero de 8 bits.
     *
     * @param valor entero a evaluar
     * @return true si esta entre -127 y 127 inclusive
     */
    public static boolean esRepresentable(int valor) {
        return valor >= VALOR_MINIMO && valor <= VALOR_MAXIMO;
    }

    /**
     * Convierte un entero de Java al byte de 8 bits en signo-magnitud.
     *
     * El cero siempre se normaliza a 00000000. El formato admite dos ceros
     * (00000000 y 10000000) pero solo se genera el positivo.
     *
     * @param valor entero entre -127 y 127
     * @return byte en signo-magnitud, en los 8 bits bajos del int
     * @throws DesbordamientoException si el valor esta fuera de rango
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
     * Convierte un byte en signo-magnitud al entero de Java equivalente.
     *
     * Solo se consideran los 8 bits bajos del argumento. El cero negativo
     * (10000000) se interpreta como 0.
     *
     * @param byteSignoMagnitud patron de 8 bits
     * @return entero entre -127 y 127
     */
    public static int aEntero(int byteSignoMagnitud) {
        int patron = byteSignoMagnitud & 0xFF;
        int magnitud = patron & MASCARA_MAGNITUD;
        boolean negativo = (patron & MASCARA_SIGNO) != 0;
        return negativo ? -magnitud : magnitud;
    }

    /**
     * Representa un patron de bits en binario, con ceros a la izquierda y
     * sin separadores.
     *
     * Trabaja sobre el patron crudo, no sobre el valor con signo. Para
     * mostrar un entero del Mini PC usar {@link #aBinarioEntero(int)}.
     *
     * @param patron patron de bits a representar
     * @param bits   cantidad de bits a mostrar, entre 1 y 32
     * @return cadena binaria de la longitud pedida
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
     * Representa un entero del Mini PC como los 8 bits de signo-magnitud
     * que le corresponden.
     *
     * @param valor entero entre -127 y 127
     * @return cadena binaria de 8 caracteres
     * @throws DesbordamientoException si el valor esta fuera de rango
     */
    public static String aBinarioEntero(int valor) {
        return aBinario(aSignoMagnitud(valor), 8);
    }

    /**
     * Representa una palabra de instruccion de 16 bits con la agrupacion
     * del enunciado: opcode, registro y operando.
     *
     * Ejemplo: MOV AX, 5 se muestra como "0011 0001 00000101".
     *
     * @param palabra palabra de 16 bits
     * @return cadena con los tres campos separados por espacios
     */
    public static String aBinarioPalabra(int palabra) {
        int p = palabra & 0xFFFF;
        return aBinario(p >>> 12, 4) + " "
                + aBinario((p >>> 8) & 0xF, 4) + " "
                + aBinario(p & 0xFF, 8);
    }
}
