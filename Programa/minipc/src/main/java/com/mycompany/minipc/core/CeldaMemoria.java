package com.mycompany.minipc.core;

import com.mycompany.minipc.util.BinUtil;

/**
 * Una posicion de memoria del Mini PC.
 *
 * Cada celda guarda una palabra de 16 bits. El enunciado pide que cada
 * linea de programa ocupe una posicion, y una instruccion codificada cabe
 * completa en una palabra, asi que la correspondencia es directa: una
 * linea, una celda.
 *
 * La etiqueta conserva el texto original de la instruccion para poder
 * mostrarlo en la tabla de memoria de la interfaz, como en la maqueta del
 * enunciado donde la posicion 21 muestra "MOV AX, 5".
 */
public class CeldaMemoria {

    /**
     * Para que sirve la celda. Determina ademas el color con que la
     * interfaz la pinta en la tabla de memoria.
     */
    public enum Tipo {

        /** Disponible, nunca se escribio o se limpio. */
        LIBRE,

        /** Contiene una instruccion del programa del usuario. */
        INSTRUCCION,

        /**
         * Contiene un dato suelto.
         *
         * En este juego de instrucciones ninguna operacion escribe datos
         * en memoria: STORE copia el acumulador a un registro, no a una
         * direccion. El tipo queda previsto para una eventual extension,
         * pero hoy la zona de usuario solo guarda codigo.
         */
        DATO,

        /** Pertenece a la zona del sistema operativo. */
        RESERVADA_KERNEL
    }

    private int palabra;
    private Tipo tipo;
    private String etiqueta;

    /**
     * Crea una celda libre y vacia.
     */
    public CeldaMemoria() {
        limpiar();
    }

    public int getPalabra() {
        return palabra;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Escribe contenido en la celda.
     *
     * @param palabra   los 16 bits a guardar
     * @param tipo      para que queda destinada la celda
     * @param etiqueta  texto legible a mostrar, puede ser nulo
     */
    public void escribir(int palabra, Tipo tipo, String etiqueta) {
        this.palabra = palabra & 0xFFFF;
        this.tipo = tipo;
        this.etiqueta = etiqueta;
    }

    /**
     * Deja la celda libre y en cero.
     */
    public final void limpiar() {
        this.palabra = 0;
        this.tipo = Tipo.LIBRE;
        this.etiqueta = "";
    }

    /**
     * @return true si la celda no tiene contenido util
     */
    public boolean estaLibre() {
        return tipo == Tipo.LIBRE;
    }

    /**
     * @return la palabra en binario, agrupada como "0011 0001 00000101",
     *         o cadena vacia si la celda esta libre
     */
    public String getBinario() {
        return estaLibre() ? "" : BinUtil.aBinarioPalabra(palabra);
    }

    @Override
    public String toString() {
        return estaLibre() ? "[libre]" : etiqueta;
    }
}
