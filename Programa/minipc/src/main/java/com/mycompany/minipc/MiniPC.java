package com.mycompany.minipc;

import com.mycompany.minipc.gui.VentanaPrincipal;

/**
 * Nombre: MiniPC
 * Entradas: no aplica, solo expone el punto de entrada
 * Salidas: no aplica
 * Restricciones: ninguna
 * Descripcion: clase de arranque del simulador. Su unica tarea es levantar
 *              la ventana principal.
 */
public class MiniPC {

    /**
     * Nombre: main
     * Entradas: args, argumentos de linea de comandos que no se usan
     * Salidas: ninguna
     * Restricciones: la ventana debe crearse dentro del hilo de despacho de
     *                eventos, que es donde Swing exige que se creen y
     *                manipulen los componentes
     * Descripcion: encola la construccion y el despliegue de la ventana
     *              principal con invokeLater, en lugar de crearla en el hilo
     *              main, que es la causa habitual de fallos intermitentes de
     *              dibujado en aplicaciones Swing.
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VentanaPrincipal().setVisible(true);
            }
        });
    }
}
