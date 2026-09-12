package com.mycompany.minipc;

import com.mycompany.minipc.gui.VentanaPrincipal;

/**
 * Punto de entrada del simulador Mini PC.
 *
 * Levanta la ventana principal dentro del hilo de despacho de eventos de
 * Swing, que es donde Swing exige que se creen y manipulen los componentes.
 */
public class MiniPC {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VentanaPrincipal().setVisible(true);
            }
        });
    }
}
