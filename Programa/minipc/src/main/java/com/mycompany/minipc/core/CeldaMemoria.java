package com.mycompany.minipc.core;

import com.mycompany.minipc.util.BinUtil;

/**
 * Nombre: CeldaMemoria
 * Entradas: no aplica, se crea vacia y se llena despues
 * Salidas: no aplica
 * Restricciones: la palabra almacenada se recorta siempre a dieciseis bits
 * Descripcion: una posicion de memoria del Mini PC. Cada celda guarda una
 *              palabra de dieciseis bits. El enunciado pide que cada linea de
 *              programa ocupe una posicion, y una instruccion codificada cabe
 *              completa en una palabra, asi que la correspondencia es directa:
 *              una linea, una celda. La etiqueta conserva el texto original
 *              de la instruccion para mostrarlo en la tabla de memoria, como
 *              en la maqueta del enunciado donde la posicion 21 muestra
 *              "MOV AX, 5".
 */
public class CeldaMemoria {

    /**
     * Nombre: Tipo
     * Entradas: no aplica, es una enumeracion de valores fijos
     * Salidas: no aplica
     * Restricciones: ninguna
     * Descripcion: para que sirve la celda. Determina ademas el color con que
     *              la interfaz la pinta en la tabla de memoria.
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
     * Nombre: CeldaMemoria
     * Entradas: ninguna
     * Salidas: la celda construida, libre y en cero
     * Restricciones: ninguna
     * Descripcion: crea una celda vacia delegando en limpiar(), de modo que
     *              el estado inicial y el estado tras limpiar son el mismo.
     */
    public CeldaMemoria() {
        limpiar();
    }

    /**
     * Nombre: getPalabra
     * Entradas: ninguna
     * Salidas: los dieciseis bits almacenados
     * Restricciones: ninguna
     * Descripcion: lo lee el procesador en la etapa de fetch para cargar el
     *              registro de instruccion.
     */
    public int getPalabra() {
        return palabra;
    }

    /**
     * Nombre: getTipo
     * Entradas: ninguna
     * Salidas: para que esta destinada la celda
     * Restricciones: ninguna
     * Descripcion: lo consulta el renderer de la tabla de memoria para elegir
     *              el color de la fila.
     */
    public Tipo getTipo() {
        return tipo;
    }

    /**
     * Nombre: getEtiqueta
     * Entradas: ninguna
     * Salidas: el texto legible de lo que guarda la celda
     * Restricciones: puede ser cadena vacia si la celda esta libre
     * Descripcion: devuelve la linea original del programa, para mostrarla en
     *              la columna Contenido de la tabla de memoria.
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Nombre: escribir
     * Entradas: palabra, los dieciseis bits a guardar; tipo, destino de la
     *           celda; etiqueta, texto legible que puede ser nulo
     * Salidas: ninguna
     * Restricciones: de la palabra solo se conservan los dieciseis bits bajos
     * Descripcion: escribe contenido en la celda, fijando de una vez los tres
     *              campos para que nunca queden en un estado incoherente.
     */
    public void escribir(int palabra, Tipo tipo, String etiqueta) {
        this.palabra = palabra & 0xFFFF;
        this.tipo = tipo;
        this.etiqueta = etiqueta;
    }

    /**
     * Nombre: limpiar
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: es final porque el constructor la invoca
     * Descripcion: deja la celda libre, en cero y sin etiqueta.
     */
    public final void limpiar() {
        this.palabra = 0;
        this.tipo = Tipo.LIBRE;
        this.etiqueta = "";
    }

    /**
     * Nombre: estaLibre
     * Entradas: ninguna
     * Salidas: true si la celda no tiene contenido util
     * Restricciones: ninguna
     * Descripcion: evita que quien consulte tenga que comparar contra el
     *              valor concreto del enum.
     */
    public boolean estaLibre() {
        return tipo == Tipo.LIBRE;
    }

    /**
     * Nombre: getBinario
     * Entradas: ninguna
     * Salidas: la palabra en binario agrupada, o cadena vacia si esta libre
     * Restricciones: ninguna
     * Descripcion: devuelve por ejemplo "0011 0001 00000101". Una celda libre
     *              devuelve vacio en lugar de dieciseis ceros, porque mostrar
     *              ceros sugeriria que guarda algo.
     */
    public String getBinario() {
        return estaLibre() ? "" : BinUtil.aBinarioPalabra(palabra);
    }

    /**
     * Nombre: toString
     * Entradas: ninguna
     * Salidas: representacion legible de la celda
     * Restricciones: ninguna
     * Descripcion: devuelve la etiqueta, o la marca "[libre]" si no guarda
     *              nada. Pensado para depuracion.
     */
    @Override
    public String toString() {
        return estaLibre() ? "[libre]" : etiqueta;
    }
}
