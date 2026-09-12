package com.mycompany.minipc.gui.modelo;

import com.mycompany.minipc.core.CeldaMemoria;
import com.mycompany.minipc.core.Memoria;

import javax.swing.table.AbstractTableModel;

/**
 * Modelo de la tabla de memoria.
 *
 * No guarda copia de los datos: lee directamente de la memoria del
 * procesador cada vez que la tabla se dibuja. Asi nunca queda desfasado
 * respecto al estado real, y refrescar la vista se reduce a disparar
 * fireTableDataChanged.
 */
public class ModeloTablaMemoria extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNAS = {"Pos", "Zona", "Contenido", "Binario"};

    private final Memoria memoria;

    /**
     * @param memoria memoria a reflejar en la tabla
     */
    public ModeloTablaMemoria(Memoria memoria) {
        this.memoria = memoria;
    }

    @Override
    public int getRowCount() {
        return memoria.getTamano();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        CeldaMemoria celda = memoria.leer(fila);
        switch (columna) {
            case 0:
                return fila;
            case 1:
                return memoria.esDireccionKernel(fila) ? "Kernel" : "Usuario";
            case 2:
                return contenidoDe(celda, fila);
            case 3:
                return celda.getBinario();
            default:
                return "";
        }
    }

    private String contenidoDe(CeldaMemoria celda, int fila) {
        if (!celda.estaLibre() && celda.getTipo() != CeldaMemoria.Tipo.RESERVADA_KERNEL) {
            return celda.getEtiqueta();
        }
        return memoria.esDireccionKernel(fila) ? "[reservada]" : "";
    }
}
