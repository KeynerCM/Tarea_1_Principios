package com.mycompany.minipc.gui.modelo;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/**
 * Pinta la tabla de instrucciones resaltando la que apunta el PC.
 *
 * Ver de un vistazo que instruccion sigue es lo que hace entendible la
 * ejecucion paso a paso, que es justamente lo que se evalua.
 */
public class RenderInstruccionActual extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    /** Fondo de la fila que esta por ejecutarse. */
    private static final Color FONDO_ACTUAL = new Color(255, 235, 156);

    /** Texto de la fila que esta por ejecutarse. */
    private static final Color TEXTO_ACTUAL = new Color(70, 50, 0);

    /** Filas que ya se ejecutaron. */
    private static final Color FONDO_EJECUTADA = new Color(240, 240, 240);

    private static final Color TEXTO_EJECUTADA = new Color(120, 120, 120);

    private int filaActual = -1;

    /**
     * @param filaActual fila a resaltar, o -1 para no resaltar ninguna
     */
    public void setFilaActual(int filaActual) {
        this.filaActual = filaActual;
    }

    public int getFilaActual() {
        return filaActual;
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor,
            boolean seleccionada, boolean tieneFoco, int fila, int columna) {

        Component celda = super.getTableCellRendererComponent(
                tabla, valor, seleccionada, tieneFoco, fila, columna);

        // La columna del binario se lee mucho mejor en fuente monoespaciada.
        celda.setFont(columna == 2
                ? new Font(Font.MONOSPACED, Font.PLAIN, 12)
                : tabla.getFont());

        if (seleccionada) {
            return celda;
        }

        if (fila == filaActual) {
            celda.setBackground(FONDO_ACTUAL);
            celda.setForeground(TEXTO_ACTUAL);
            celda.setFont(celda.getFont().deriveFont(Font.BOLD));
        } else if (filaActual >= 0 && fila < filaActual) {
            celda.setBackground(FONDO_EJECUTADA);
            celda.setForeground(TEXTO_EJECUTADA);
        } else {
            celda.setBackground(tabla.getBackground());
            celda.setForeground(tabla.getForeground());
        }
        return celda;
    }
}
