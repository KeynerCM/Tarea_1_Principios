package com.mycompany.minipc.excepciones;

/**
 * Se lanza cuando la zona de usuario no tiene posiciones libres suficientes
 * para el programa que se intenta cargar.
 *
 * La carga es atomica: si esta excepcion se lanza, la memoria queda tal
 * como estaba y no se escribio ninguna instruccion.
 */
public class MemoriaInsuficienteException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int requeridas;
    private final int disponibles;

    /**
     * @param requeridas  posiciones que necesita el programa
     * @param disponibles posiciones libres en la zona de usuario
     */
    public MemoriaInsuficienteException(int requeridas, int disponibles) {
        super("El programa requiere " + requeridas + " posiciones y la zona de usuario"
                + " solo dispone de " + disponibles);
        this.requeridas = requeridas;
        this.disponibles = disponibles;
    }

    public int getRequeridas() {
        return requeridas;
    }

    public int getDisponibles() {
        return disponibles;
    }
}
