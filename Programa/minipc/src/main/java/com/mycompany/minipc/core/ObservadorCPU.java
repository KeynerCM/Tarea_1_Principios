package com.mycompany.minipc.core;

/**
 * Quien quiera enterarse de lo que va haciendo el procesador implementa
 * esta interfaz y se registra en el.
 *
 * Es el patron Observer. Sirve para que el procesador no tenga que conocer
 * a Swing: el nucleo avisa que algo cambio y la interfaz decide como se
 * dibuja. Asi el simulador se puede probar entero sin levantar ventanas.
 */
public interface ObservadorCPU {

    /**
     * @param cpu  el procesador que cambio de estado
     * @param fase en que momento del ciclo esta avisando
     */
    void alCambiarEstado(Procesador cpu, Fase fase);
}
