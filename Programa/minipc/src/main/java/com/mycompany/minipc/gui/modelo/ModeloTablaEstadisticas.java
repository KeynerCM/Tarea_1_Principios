package com.mycompany.minipc.gui.modelo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Nombre: ModeloTablaEstadisticas
 * Entradas: las metricas que se le vayan agregando, con su valor
 * Salidas: el contenido de cada celda que la tabla pida dibujar
 * Restricciones: las celdas no son editables; los valores se guardan ya
 *                convertidos a texto, de modo que el modelo no los formatea
 * Descripcion: modelo de la tabla de estadisticas, con una metrica por fila y
 *              su valor al lado. Es deliberadamente generico: quien lo llena
 *              decide que metricas mostrar y en que orden, asi que agregar una
 *              medicion nueva no obliga a tocar esta clase.
 */
public class ModeloTablaEstadisticas extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNAS = {"Metrica", "Valor"};

    private final List<String[]> filas = new ArrayList<>();

    /**
     * Nombre: agregar
     * Entradas: metrica, nombre de la medicion; valor, dato a mostrar
     * Salidas: ninguna
     * Restricciones: no avisa a la tabla; hay que llamar a refrescar() cuando
     *                se termine de agregar todo
     * Descripcion: agrega una metrica al final de la tabla, convirtiendo el
     *              valor a texto. No notifica en cada agregado para no
     *              redibujar la tabla una vez por fila.
     */
    public void agregar(String metrica, Object valor) {
        filas.add(new String[]{metrica, String.valueOf(valor)});
    }

    /**
     * Nombre: limpiar
     * Entradas: ninguna
     * Salidas: ninguna; avisa a la tabla de que debe redibujarse
     * Restricciones: ninguna
     * Descripcion: vacia la tabla por completo.
     */
    public void limpiar() {
        filas.clear();
        fireTableDataChanged();
    }

    /**
     * Nombre: refrescar
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: ninguna
     * Descripcion: avisa a Swing de que el contenido cambio, para llamarla una
     *              sola vez despues de agregar todas las metricas.
     */
    public void refrescar() {
        fireTableDataChanged();
    }

    /**
     * Nombre: getRowCount
     * Entradas: ninguna
     * Salidas: cuantas metricas se agregaron
     * Restricciones: ninguna
     * Descripcion: Swing la consulta para saber cuantas filas dibujar.
     */
    @Override
    public int getRowCount() {
        return filas.size();
    }

    /**
     * Nombre: getColumnCount
     * Entradas: ninguna
     * Salidas: cuantas columnas tiene la tabla, siempre dos
     * Restricciones: ninguna
     * Descripcion: Swing la consulta para saber cuantas columnas dibujar.
     */
    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    /**
     * Nombre: getColumnName
     * Entradas: columna, indice de la columna desde cero
     * Salidas: el titulo que se muestra en el encabezado
     * Restricciones: el indice debe estar dentro del rango de columnas
     * Descripcion: devuelve el encabezado correspondiente.
     */
    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    /**
     * Nombre: isCellEditable
     * Entradas: fila y columna de la celda consultada
     * Salidas: siempre false
     * Restricciones: ninguna
     * Descripcion: las estadisticas son un resultado de la ejecucion y no
     *              tiene sentido editarlas.
     */
    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    /**
     * Nombre: getValueAt
     * Entradas: fila, metrica pedida; columna, nombre o valor
     * Salidas: el texto correspondiente
     * Restricciones: la fila debe existir entre las metricas agregadas
     * Descripcion: devuelve el elemento de la pareja nombre-valor que ocupa
     *              esa posicion.
     */
    @Override
    public Object getValueAt(int fila, int columna) {
        return filas.get(fila)[columna];
    }
}
