package com.mycompany.minipc.excepciones;

/**
 * Nombre: MemoriaInsuficienteException
 * Entradas: la cantidad de posiciones requeridas y la cantidad disponible
 * Salidas: no aplica
 * Restricciones: es una excepcion verificada, por lo que quien cargue un
 *                programa esta obligado a contemplar el caso
 * Descripcion: se lanza cuando la zona de usuario no tiene posiciones libres
 *              suficientes para el programa que se intenta cargar. La carga
 *              es atomica: si esta excepcion se lanza, la memoria queda tal
 *              como estaba y no se escribio ninguna instruccion.
 */
public class MemoriaInsuficienteException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int requeridas;
    private final int disponibles;

    /**
     * Nombre: MemoriaInsuficienteException
     * Entradas: requeridas, posiciones que necesita el programa; disponibles,
     *           posiciones libres en la zona de usuario
     * Salidas: la excepcion construida, con el mensaje ya armado
     * Restricciones: se espera que requeridas sea mayor que disponibles, que
     *                es la situacion que justifica lanzarla
     * Descripcion: guarda ambas cantidades y compone un mensaje que nombra
     *              las dos, para que el usuario sepa cuanto le falta y no
     *              solo que no cupo.
     */
    public MemoriaInsuficienteException(int requeridas, int disponibles) {
        super("El programa requiere " + requeridas + " posiciones y la zona de usuario"
                + " solo dispone de " + disponibles);
        this.requeridas = requeridas;
        this.disponibles = disponibles;
    }

    /**
     * Nombre: getRequeridas
     * Entradas: ninguna
     * Salidas: cantidad de posiciones que necesitaba el programa
     * Restricciones: ninguna
     * Descripcion: permite a la interfaz mostrar el dato por separado en vez
     *              de tener que interpretar el mensaje de texto.
     */
    public int getRequeridas() {
        return requeridas;
    }

    /**
     * Nombre: getDisponibles
     * Entradas: ninguna
     * Salidas: cantidad de posiciones libres que habia en la zona de usuario
     * Restricciones: ninguna
     * Descripcion: complemento de getRequeridas, con el mismo proposito.
     */
    public int getDisponibles() {
        return disponibles;
    }
}
