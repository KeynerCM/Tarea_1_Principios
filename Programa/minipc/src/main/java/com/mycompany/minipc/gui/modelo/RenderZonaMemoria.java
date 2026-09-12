package com.mycompany.minipc.gui.modelo;

import com.mycompany.minipc.core.CeldaMemoria;
import com.mycompany.minipc.core.Memoria;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/**
 * Pinta la tabla de memoria distinguiendo las zonas.
 *
 * La separacion entre kernel y usuario es uno de los requisitos del
 * enunciado, y verla en colores la vuelve evidente sin tener que leer
 * numeros de direccion.
 */
public class RenderZonaMemoria extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    /** Zona reservada al sistema operativo. */
    private static final Color FONDO_KERNEL = new Color(232, 232, 232);

    private static final Color TEXTO_KERNEL = new Color(120, 120, 120);

    /** Zona de usuario ocupada por instrucciones del programa. */
    private static final Color FONDO_INSTRUCCION = new Color(214, 234, 248);

    /** Posicion que apunta el PC en este momento. */
    private static final Color FONDO_ACTUAL = new Color(255, 235, 156);

    private static final Color TEXTO_ACTUAL = new Color(70, 50, 0);

    private final Memoria memoria;
    private int direccionActual = -1;

    /**
     * @param memoria memoria que se esta dibujando
     */
    public RenderZonaMemoria(Memoria memoria) {
        this.memoria = memoria;
    }

    /**
     * @param direccionActual posicion que apunta el PC, o -1 si no aplica
     */
    public void setDireccionActual(int direccionActual) {
        this.direccionActual = direccionActual;
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor,
            boolean seleccionada, boolean tieneFoco, int fila, int columna) {

        Component celda = super.getTableCellRendererComponent(
                tabla, valor, seleccionada, tieneFoco, fila, columna);

        celda.setFont(columna == 3
                ? new Font(Font.MONOSPACED, Font.PLAIN, 12)
                : tabla.getFont());

        if (seleccionada) {
            return celda;
        }

        celda.setForeground(tabla.getForeground());

        if (fila == direccionActual) {
            celda.setBackground(FONDO_ACTUAL);
            celda.setForeground(TEXTO_ACTUAL);
            celda.setFont(celda.getFont().deriveFont(Font.BOLD));
        } else if (memoria.esDireccionKernel(fila)) {
            celda.setBackground(FONDO_KERNEL);
            celda.setForeground(TEXTO_KERNEL);
        } else if (memoria.leer(fila).getTipo() == CeldaMemoria.Tipo.INSTRUCCION) {
            celda.setBackground(FONDO_INSTRUCCION);
        } else {
            celda.setBackground(tabla.getBackground());
        }
        return celda;
    }
}
