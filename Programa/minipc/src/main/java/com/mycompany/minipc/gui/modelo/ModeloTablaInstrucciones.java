package com.mycompany.minipc.gui.modelo;

import com.mycompany.minipc.isa.Instruccion;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Nombre: ModeloTablaInstrucciones
 * Entradas: la lista de instrucciones ya ensambladas
 * Salidas: el contenido de cada celda que la tabla pida dibujar
 * Restricciones: las celdas no son editables; el modelo guarda su propia
 *                copia de la lista
 * Descripcion: modelo de la tabla de instrucciones, que muestra el programa
 *              fuente junto a su traduccion binaria como en la maqueta del
 *              enunciado. Las columnas son el numero de orden, la instruccion
 *              tal como fue escrita y su codificacion de dieciseis bits.
 */
public class ModeloTablaInstrucciones extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNAS = {"#", "Instruccion", "Binario"};

    private final List<Instruccion> programa = new ArrayList<>();

    /**
     * Nombre: cargar
     * Entradas: nuevas, instrucciones a mostrar, o nulo para vaciar la tabla
     * Salidas: ninguna; avisa a la tabla de que debe redibujarse
     * Restricciones: reemplaza el contenido anterior, no lo agrega
     * Descripcion: sustituye el programa que muestra la tabla y dispara el
     *              evento que hace que Swing la vuelva a dibujar.
     */
    public void cargar(List<Instruccion> nuevas) {
        programa.clear();
        if (nuevas != null) {
            programa.addAll(nuevas);
        }
        fireTableDataChanged();
    }

    /**
     * Nombre: limpiar
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: ninguna
     * Descripcion: vacia la tabla, delegando en cargar() para no repetir la
     *              notificacion a Swing.
     */
    public void limpiar() {
        cargar(null);
    }

    /**
     * Nombre: getRowCount
     * Entradas: ninguna
     * Salidas: cuantas instrucciones tiene el programa
     * Restricciones: ninguna
     * Descripcion: Swing la consulta para saber cuantas filas dibujar.
     */
    @Override
    public int getRowCount() {
        return programa.size();
    }

    /**
     * Nombre: getColumnCount
     * Entradas: ninguna
     * Salidas: cuantas columnas tiene la tabla, siempre tres
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
     * Descripcion: la tabla es de solo lectura; el programa se modifica
     *              editando el archivo .asm y volviendo a cargarlo, no
     *              escribiendo sobre la pantalla.
     */
    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    /**
     * Nombre: getValueAt
     * Entradas: fila, instruccion a mostrar; columna, dato pedido
     * Salidas: el numero de orden, el texto fuente o el binario
     * Restricciones: la fila debe existir en el programa cargado
     * Descripcion: traduce la posicion de la celda al dato correspondiente de
     *              la instruccion. La primera columna muestra el numero de
     *              orden empezando en uno, que es como lo lee una persona.
     */
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
