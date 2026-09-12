package com.mycompany.minipc.gui;

import com.mycompany.minipc.core.BCP;
import com.mycompany.minipc.isa.Instruccion;

import java.io.File;
import java.util.List;

/**
 * Contrato entre el controlador y la ventana.
 *
 * El controlador programa siempre contra esta interfaz y nunca contra la
 * clase concreta. Asi la logica de la aplicacion no depende de como esta
 * construida la ventana, y se puede sustituir por una implementacion de
 * prueba sin levantar Swing.
 */
public interface VistaPrincipal {

    /**
     * Llena la tabla de instrucciones con el programa recien ensamblado.
     *
     * @param programa instrucciones traducidas, en orden
     */
    void mostrarInstrucciones(List<Instruccion> programa);

    /**
     * Marca cual instruccion esta por ejecutarse.
     *
     * @param indiceFila fila a resaltar, o -1 para quitar el resaltado
     */
    void resaltarInstruccion(int indiceFila);

    /**
     * Vuelve a dibujar la tabla de memoria, cuyo contenido cambio.
     */
    void refrescarMemoria();

    /**
     * Muestra el contenido del bloque de control de proceso.
     *
     * @param bcp bloque a mostrar, o null para dejar el panel en blanco
     */
    void mostrarBCP(BCP bcp);

    /**
     * Agrega una linea al registro de actividad, con su hora.
     *
     * @param mensaje texto a mostrar
     */
    void escribirEnConsola(String mensaje);

    /**
     * Vacia el registro de actividad.
     */
    void limpiarConsola();

    /**
     * Muestra una lista de errores en un cuadro de dialogo.
     *
     * @param titulo   titulo del cuadro
     * @param mensajes errores a mostrar, uno por linea
     */
    void mostrarErrores(String titulo, List<String> mensajes);

    /**
     * Habilita o deshabilita los botones segun la situacion actual.
     *
     * @param hayPrograma  hay un programa cargado en memoria
     * @param enEjecucion  la ejecucion automatica esta en marcha
     * @param termino      el programa llego al final o quedo bloqueado
     */
    void actualizarBotones(boolean hayPrograma, boolean enEjecucion, boolean termino);

    /**
     * Actualiza la linea de contexto bajo la barra de herramientas.
     *
     * @param nombreArchivo nombre del archivo cargado
     * @param estado        estado del proceso, en texto
     */
    void actualizarBarraContexto(String nombreArchivo, String estado);

    /**
     * Pide al usuario que elija un archivo de codigo ensamblador.
     *
     * @return el archivo elegido, o null si cancelo
     */
    File seleccionarArchivoAsm();
}
