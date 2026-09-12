package com.mycompany.minipc.gui.modelo;

import com.mycompany.minipc.isa.Instruccion;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de la tabla de instrucciones: el programa fuente junto a su
 * traduccion binaria, como en la maqueta del enunciado.
 */
public class ModeloTablaInstrucciones extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNAS = {"#", "Instruccion", "Binario"};

    private final List<Instruccion> programa = new ArrayList<>();

    /**
     * Reemplaza el contenido de la tabla.
     *
     * @param nuevas instrucciones a mostrar, o null para vaciarla
     */
    public void cargar(List<Instruccion> nuevas) {
        programa.clear();
        if (nuevas != null) {
            programa.addAll(nuevas);
        }
        fireTableDataChanged();
    }

    /**
     * Vacia la tabla.
     */
    public void limpiar() {
        cargar(null);
    }

    @Override
    public int getRowCount() {
        return programa.size();
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
        Instruccion instruccion = programa.get(fila);
        switch (columna) {
            case 0:
                return fila + 1;
            case 1:
                return instruccion.getTextoFuente();
            case 2:
                return instruccion.aBinarioFormateado();
            default:
                return "";
        }
    }
}
