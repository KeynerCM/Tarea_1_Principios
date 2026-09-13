package com.mycompany.minipc.gui.modelo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de la tabla de estadisticas: una metrica por fila con su valor.
 *
 * Es deliberadamente generico. Quien lo llena decide que metricas mostrar
 * y en que orden, asi que agregar una medicion nueva no obliga a tocar
 * esta clase.
 */
public class ModeloTablaEstadisticas extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNAS = {"Metrica", "Valor"};

    private final List<String[]> filas = new ArrayList<>();

    /**
     * Agrega una metrica al final de la tabla.
     *
     * @param metrica nombre de la medicion
     * @param valor   valor a mostrar, ya formateado
     */
    public void agregar(String metrica, Object valor) {
        filas.add(new String[]{metrica, String.valueOf(valor)});
    }

    /**
     * Vacia la tabla.
     */
    public void limpiar() {
        filas.clear();
        fireTableDataChanged();
    }

    /**
     * Avisa a la tabla de que el contenido cambio.
     */
    public void refrescar() {
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return filas.size();
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
        return filas.get(fila)[columna];
    }
}
