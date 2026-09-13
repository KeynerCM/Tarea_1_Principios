package com.mycompany.minipc.gui;

import com.mycompany.minipc.core.Estadisticas;
import com.mycompany.minipc.core.Fase;
import com.mycompany.minipc.core.ObservadorCPU;
import com.mycompany.minipc.core.Procesador;
import com.mycompany.minipc.excepciones.DesbordamientoException;
import com.mycompany.minipc.excepciones.MemoriaInsuficienteException;
import com.mycompany.minipc.excepciones.SintaxisException;
import com.mycompany.minipc.gui.modelo.ModeloTablaInstrucciones;
import com.mycompany.minipc.gui.modelo.ModeloTablaMemoria;
import com.mycompany.minipc.gui.modelo.RenderInstruccionActual;
import com.mycompany.minipc.gui.modelo.RenderZonaMemoria;
import com.mycompany.minipc.io.CargadorASM;
import com.mycompany.minipc.isa.Ensamblador;
import com.mycompany.minipc.isa.Instruccion;

import javax.swing.Timer;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Coordina la ventana con el procesador.
 *
 * Es lo unico que conoce a los dos lados: recibe lo que el usuario pulsa,
 * se lo pide al nucleo, y refresca la vista cuando el nucleo avisa que
 * algo cambio. El nucleo nunca sabe que existe Swing.
 */
public class ControladorPrincipal implements ObservadorCPU {

    /** Milisegundos entre instrucciones en la ejecucion automatica. */
    public static final int VELOCIDAD_POR_DEFECTO = 500;

    private final VistaPrincipal vista;
    private final Procesador cpu;
    private final CargadorASM cargador;
    private final Ensamblador ensamblador;

    private final ModeloTablaInstrucciones modeloInstrucciones;
    private final ModeloTablaMemoria modeloMemoria;
    private final RenderInstruccionActual renderInstrucciones;
    private final RenderZonaMemoria renderMemoria;

    /**
     * Temporizador de la ejecucion automatica.
     *
     * Se usa un javax.swing.Timer y no un bucle porque sus disparos ocurren
     * en el hilo de despacho de eventos. Un while llamando a paso() dentro
     * de ese hilo congelaria la ventana hasta terminar, y no se veria nada
     * de la ejecucion, que es justo lo que hay que mostrar.
     */
    private final Timer temporizador;

    private String nombreArchivo;

    /**
     * @param vista ventana a la que este controlador da servicio
     */
    public ControladorPrincipal(VistaPrincipal vista) {
        this.vista = vista;
        this.cpu = new Procesador();
        this.cargador = new CargadorASM();
        this.ensamblador = new Ensamblador();
        this.nombreArchivo = "(ninguno)";

        this.modeloInstrucciones = new ModeloTablaInstrucciones();
        this.modeloMemoria = new ModeloTablaMemoria(cpu.getMemoria());
        this.renderInstrucciones = new RenderInstruccionActual();
        this.renderMemoria = new RenderZonaMemoria(cpu.getMemoria());

        this.temporizador = new Timer(VELOCIDAD_POR_DEFECTO, e -> alTicDelTemporizador());
        this.cpu.agregarObservador(this);
    }

    // ------------------------------------------------------------------
    // Acciones de los botones
    // ------------------------------------------------------------------

    /**
     * Pide un archivo, lo ensambla y lo carga en memoria.
     *
     * Si algo falla, no se carga nada: la memoria queda como estaba y el
     * problema se informa al usuario con todo el detalle disponible.
     */
    public void alCargarArchivo() {
        File archivo = vista.seleccionarArchivoAsm();
        if (archivo == null) {
            return;
        }
        try {
            List<String> lineas = cargador.leer(archivo);
            List<Instruccion> programa = ensamblador.ensamblar(lineas);
            cpu.cargar(programa, archivo.getName());

            nombreArchivo = archivo.getName();
            modeloInstrucciones.cargar(programa);
            vista.mostrarInstrucciones(programa);
            vista.escribirEnConsola("Programa cargado en la posicion "
                    + cpu.getDireccionBase() + ". " + programa.size() + " instrucciones.");
            actualizarVista();

        } catch (SintaxisException e) {
            vista.mostrarErrores("Errores de sintaxis", e.getErrores());
            vista.escribirEnConsola("El archivo " + archivo.getName() + " tiene "
                    + e.cantidad() + " error(es) de sintaxis. No se cargo nada.");

        } catch (MemoriaInsuficienteException e) {
            vista.mostrarErrores("Memoria insuficiente",
                    Collections.singletonList(e.getMessage()));
            vista.escribirEnConsola(e.getMessage());

        } catch (IOException | IllegalArgumentException e) {
            vista.mostrarErrores("No se pudo leer el archivo",
                    Collections.singletonList(e.getMessage()));
            vista.escribirEnConsola("Error al leer el archivo: " + e.getMessage());
        }
    }

    /**
     * Arranca la ejecucion automatica hasta el final del programa.
     */
    public void alEjecutar() {
        if (!cpu.hayPrograma() || cpu.haTerminado() || temporizador.isRunning()) {
            return;
        }
        vista.escribirEnConsola("Ejecucion automatica iniciada.");
        temporizador.start();
        actualizarVista();
    }

    /**
     * Ejecuta una sola instruccion.
     */
    public void alPasoAPaso() {
        if (!cpu.hayPrograma() || cpu.haTerminado() || temporizador.isRunning()) {
            return;
        }
        ejecutarUnPaso();
    }

    /**
     * Vuelve al inicio del programa sin descargarlo.
     */
    public void alReiniciar() {
        detener();
        if (!cpu.hayPrograma()) {
            return;
        }
        cpu.reset();
        vista.escribirEnConsola("Procesador reiniciado en la posicion "
                + cpu.getDireccionBase() + ".");
    }

    /**
     * Descarga el programa y deja todo en blanco.
     */
    public void alLimpiar() {
        detener();
        cpu.limpiar();
        nombreArchivo = "(ninguno)";
        modeloInstrucciones.limpiar();
        vista.mostrarInstrucciones(Collections.emptyList());
        vista.limpiarConsola();
        vista.escribirEnConsola("Memoria de usuario, registros y tablas vaciados.");
        actualizarVista();
    }

    /**
     * Aplica una nueva configuracion. Descarga el programa actual, porque
     * redimensionar la memoria invalida las direcciones ya asignadas.
     *
     * @param tamanoMemoria cantidad total de posiciones
     * @param limiteKernel  primera direccion de la zona de usuario
     * @param velocidadMs   milisegundos entre instrucciones
     */
    public void alConfigurar(int tamanoMemoria, int limiteKernel, int velocidadMs) {
        detener();
        try {
            cpu.configurarMemoria(tamanoMemoria, limiteKernel);
            temporizador.setDelay(velocidadMs);

            nombreArchivo = "(ninguno)";
            modeloInstrucciones.limpiar();
            vista.mostrarInstrucciones(Collections.emptyList());
            vista.escribirEnConsola("Memoria configurada: " + tamanoMemoria
                    + " posiciones, kernel de 0 a " + (limiteKernel - 1)
                    + ", usuario de " + limiteKernel + " a " + (tamanoMemoria - 1)
                    + ". Velocidad: " + velocidadMs + " ms.");
            actualizarVista();

        } catch (IllegalArgumentException e) {
            vista.mostrarErrores("Configuracion invalida",
                    Collections.singletonList(e.getMessage()));
        }
    }

    // ------------------------------------------------------------------
    // Ejecucion
    // ------------------------------------------------------------------

    private void alTicDelTemporizador() {
        if (!ejecutarUnPaso()) {
            detener();
        }
    }

    /**
     * Ejecuta una instruccion y atiende el caso de desbordamiento.
     *
     * @return true si queda alguna instruccion por ejecutar
     */
    private boolean ejecutarUnPaso() {
        try {
            boolean quedan = cpu.paso();
            if (!quedan) {
                vista.escribirEnConsola("Ejecucion terminada. "
                        + cpu.getInstruccionesEjecutadas() + " instrucciones ejecutadas.");
                actualizarVista();
            }
            return quedan;

        } catch (DesbordamientoException e) {
            // El procesador ya dejo el proceso en BLOQUEADO_ERROR y aviso a
            // los observadores, asi que la pantalla ya refleja el estado.
            detener();
            vista.escribirEnConsola("ERROR: " + e.getMessage());
            vista.mostrarErrores("Error de ejecucion",
                    Collections.singletonList(e.getMessage()));
            actualizarVista();
            return false;
        }
    }

    /**
     * Detiene la ejecucion automatica y refresca la vista.
     *
     * El refresco final no es opcional: mientras el temporizador corre, el
     * procesador notifica a los observadores antes de que este metodo lo
     * detenga, de modo que esas notificaciones ven todavia isRunning() en
     * true y dejan los botones deshabilitados. Sin este ultimo refresco la
     * ventana se queda bloqueada al terminar el programa.
     */
    private void detener() {
        if (temporizador.isRunning()) {
            temporizador.stop();
            actualizarVista();
        }
    }

    // ------------------------------------------------------------------
    // Observador del procesador
    // ------------------------------------------------------------------

    @Override
    public void alCambiarEstado(Procesador procesador, Fase fase) {
        actualizarVista();
    }

    /**
     * Vuelca el estado actual del procesador sobre la ventana.
     */
    private void actualizarVista() {
        int indice = cpu.getIndiceInstruccionActual();
        renderInstrucciones.setFilaActual(indice);
        renderMemoria.setDireccionActual(cpu.hayPrograma() && indice >= 0
                ? cpu.getPc() : -1);

        vista.resaltarInstruccion(indice);
        vista.refrescarMemoria();
        vista.mostrarBCP(cpu.getBcp());
        vista.actualizarBarraContexto(nombreArchivo, textoDelEstado());
        vista.actualizarUsoMemoria(cpu.getMemoria().getPorcentajeUso());
        vista.actualizarBotones(cpu.hayPrograma(), temporizador.isRunning(),
                cpu.haTerminado());
    }

    private String textoDelEstado() {
        return cpu.hayPrograma() ? cpu.getEstado().name() : "SIN PROGRAMA";
    }

    // ------------------------------------------------------------------
    // Acceso para la ventana
    // ------------------------------------------------------------------

    public ModeloTablaInstrucciones getModeloInstrucciones() {
        return modeloInstrucciones;
    }

    public ModeloTablaMemoria getModeloMemoria() {
        return modeloMemoria;
    }

    public RenderInstruccionActual getRenderInstrucciones() {
        return renderInstrucciones;
    }

    public RenderZonaMemoria getRenderMemoria() {
        return renderMemoria;
    }

    public Estadisticas obtenerEstadisticas() {
        return cpu.getEstadisticas();
    }

    public Procesador getProcesador() {
        return cpu;
    }

    /**
     * @return milisegundos configurados entre instrucciones
     */
    public int getVelocidadMs() {
        return temporizador.getDelay();
    }

    /**
     * Deja la ventana en su estado inicial, recien construida.
     */
    public void inicializarVista() {
        actualizarVista();
        vista.escribirEnConsola("Mini PC listo. Memoria de "
                + cpu.getMemoria().getTamano() + " posiciones, kernel de 0 a "
                + (cpu.getMemoria().getLimiteKernel() - 1) + ".");
    }
}
