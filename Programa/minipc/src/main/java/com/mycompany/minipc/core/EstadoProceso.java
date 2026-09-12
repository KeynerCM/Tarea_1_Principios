package com.mycompany.minipc.core;

/**
 * Estados por los que pasa el proceso cargado en el Mini PC.
 *
 * Corresponden al modelo de cinco estados del capitulo 3 de Stallings.
 * El simulador no multiprograma, asi que no hay estado SUSPENDIDO ni cola
 * de listos con varios procesos, pero las transiciones que si ocurren son
 * las mismas y se muestran en el panel del BCP.
 *
 * Transiciones posibles:
 *
 *   NUEVO -> LISTO           al terminar de cargarse en memoria
 *   LISTO -> EJECUCION       al ejecutarse la primera instruccion
 *   EJECUCION -> TERMINADO   al pasar el PC la ultima instruccion
 *   EJECUCION -> BLOQUEADO_ERROR  ante un desbordamiento aritmetico
 */
public enum EstadoProceso {

    /** Cargado en memoria, todavia sin admitir. */
    NUEVO,

    /** Listo para ejecutar, esperando el procesador. */
    LISTO,

    /** Ejecutandose en el procesador. */
    EJECUCION,

    /** Termino normalmente. */
    TERMINADO,

    /** Detenido por un error de ejecucion, como un desbordamiento. */
    BLOQUEADO_ERROR;

    /**
     * @return true si el proceso ya no puede seguir ejecutando
     */
    public boolean esFinal() {
        return this == TERMINADO || this == BLOQUEADO_ERROR;
    }
}
