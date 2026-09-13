package com.mycompany.minipc;

import com.mycompany.minipc.gui.VentanaPrincipal;

/**
 * Nombre: MiniPC
 * Entradas: no aplica, solo expone el punto de entrada
 * Salidas: no aplica
 * Restricciones: ninguna
 * Descripcion: clase de arranque del simulador. Su unica tarea es preparar la
 *              apariencia y levantar la ventana principal.
 */
public class MiniPC {

    /**
     * Nombre: main
     * Entradas: args, argumentos de linea de comandos que no se usan
     * Salidas: ninguna
     * Restricciones: la ventana debe crearse dentro del hilo de despacho de
     *                eventos, que es donde Swing exige que se creen y
     *                manipulen los componentes
     * Descripcion: fija la apariencia del sistema y encola la construccion de
     *              la ventana con invokeLater, en lugar de crearla en el hilo
     *              main, que es la causa habitual de fallos intermitentes de
     *              dibujado en aplicaciones Swing.
     */
    public static void main(String[] args) {
        aplicarApariencia();
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VentanaPrincipal().setVisible(true);
            }
        });
    }

    /**
     * Nombre: aplicarApariencia
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: si la apariencia del sistema no esta disponible se sigue
     *                con la que Java trae por defecto, sin interrumpir el
     *                arranque
     * Descripcion: usa la apariencia nativa del sistema operativo en lugar de
     *              Metal, que es la que Java aplica si no se indica otra y que
     *              hace que la ventana se vea ajena al escritorio.
     */
    private static void aplicarApariencia() {
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
            // La apariencia es un detalle estetico: si falla, se sigue igual.
        }
    }
}
