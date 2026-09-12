package com.mycompany.minipc.core;

/**
 * Momento del ciclo de instruccion en que el procesador avisa a sus
 * observadores.
 *
 * Las dos primeras son las etapas que describe la figura 1.2 de Stallings.
 * Las otras dos no son etapas del ciclo, pero son los momentos en que la
 * interfaz tambien necesita refrescarse.
 */
public enum Fase {

    /** Se trajo la instruccion de memoria al registro IR. */
    FETCH,

    /** Se interpreto y ejecuto la instruccion. */
    EXECUTE,

    /** Se acaba de cargar un programa en memoria. */
    CARGA,

    /** El procesador volvio al inicio del programa. */
    REINICIO
}
