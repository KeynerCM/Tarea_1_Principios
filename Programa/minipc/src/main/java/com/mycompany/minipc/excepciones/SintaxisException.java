package com.mycompany.minipc.excepciones;

import java.util.Collections;
import java.util.List;

/**
 * Nombre: SintaxisException
 * Entradas: uno o varios mensajes de error encontrados al ensamblar
 * Salidas: no aplica
 * Restricciones: es una excepcion verificada; la lista que transporta es de
 *                solo lectura
 * Descripcion: reune los errores de sintaxis de un archivo completo. El
 *              ensamblador no se detiene en el primer problema: recorre todo
 *              el archivo, junta lo que encuentra y lo reporta de una sola
 *              vez, para que el usuario corrija en una pasada en lugar de
 *              descubrir los errores uno por uno. Por eso la excepcion lleva
 *              una lista y no un solo mensaje.
 */
public class SintaxisException extends Exception {

    private static final long serialVersionUID = 1L;

    private final List<String> errores;

    /**
     * Nombre: SintaxisException
     * Entradas: errores, mensajes en el orden en que aparecen en el archivo
     * Salidas: la excepcion construida
     * Restricciones: la lista no debe ser nula; se copia, de modo que
     *                modificarla despues no afecta a la excepcion
     * Descripcion: construye la excepcion a partir de varios errores y arma
     *              el mensaje general uniendolos con saltos de linea.
     */
    public SintaxisException(List<String> errores) {
        super(String.join(System.lineSeparator(), errores));
        this.errores = List.copyOf(errores);
    }

    /**
     * Nombre: SintaxisException
     * Entradas: error, unico mensaje
     * Salidas: la excepcion construida
     * Restricciones: ninguna
     * Descripcion: atajo para el caso de un solo problema, usado cuando falla
     *              una linea concreta antes de acumularla en el conjunto.
     */
    public SintaxisException(String error) {
        super(error);
        this.errores = Collections.singletonList(error);
    }

    /**
     * Nombre: getErrores
     * Entradas: ninguna
     * Salidas: los mensajes de error, en el orden en que aparecen en el archivo
     * Restricciones: la lista devuelta es inmutable
     * Descripcion: la interfaz la usa para mostrar todos los errores juntos
     *              en un solo cuadro de dialogo.
     */
    public List<String> getErrores() {
        return errores;
    }

    /**
     * Nombre: cantidad
     * Entradas: ninguna
     * Salidas: cuantos errores se encontraron
     * Restricciones: siempre es al menos uno
     * Descripcion: evita tener que pedir la lista solo para contarla, por
     *              ejemplo al escribir el resumen en la consola.
     */
    public int cantidad() {
        return errores.size();
    }
}
