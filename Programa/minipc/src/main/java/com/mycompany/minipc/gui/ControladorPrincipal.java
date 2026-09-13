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
 * Nombre: ControladorPrincipal
 * Entradas: la vista a la que da servicio y las acciones que el usuario pulsa
 * Salidas: las actualizaciones que envia a la vista
 * Restricciones: es lo unico que conoce a los dos lados; el nucleo nunca sabe
 *                que existe Swing y la vista nunca conoce al procesador
 * Descripcion: coordina la ventana con el procesador. Recibe lo que el usuario
 *              pulsa, se lo pide al nucleo, y refresca la vista cuando el
 *              nucleo avisa que algo cambio. Implementa ObservadorCPU para
 *              enterarse de cada etapa del ciclo de instruccion.
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
     * Nombre: ControladorPrincipal
     * Entradas: vista, ventana a la que este controlador da servicio
     * Salidas: el controlador construido
     * Restricciones: la vista no debe ser nula; el controlador queda ya
     *                registrado como observador del procesador
     * Descripcion: crea el procesador, los modelos de tabla, los renderers y
     *              el temporizador. Los renderers se crean aqui y no en la
     *              ventana porque necesitan consultar la memoria, que vive
     *              dentro del procesador.
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
     * Nombre: alCargarArchivo
     * Entradas: ninguna; el archivo lo pide a la vista
     * Salidas: ninguna; deja el programa cargado y la vista actualizada
     * Restricciones: si algo falla no se carga nada y la memoria queda como
     *                estaba; si el usuario cancela el dialogo no ocurre nada
     * Descripcion: encadena las cuatro etapas de la carga: elegir archivo,
     *              leerlo, ensamblarlo y cargarlo en memoria. Cada tipo de
     *              fallo se informa con su propio titulo, de modo que el
     *              usuario sepa si el problema es del archivo, de su sintaxis
     *              o del espacio disponible.
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
     * Nombre: alEjecutar
     * Entradas: ninguna
     * Salidas: ninguna; arranca el temporizador
     * Restricciones: no hace nada si no hay programa, si ya termino, o si la
     *                ejecucion automatica ya esta en marcha
     * Descripcion: arranca la ejecucion automatica hasta el final del programa.
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
     * Nombre: alPasoAPaso
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: no hace nada si no hay programa, si ya termino, o si la
     *                ejecucion automatica esta en marcha
     * Descripcion: ejecuta una sola instruccion, que es el modo de ejecucion
     *              que el enunciado exige.
     */
    public void alPasoAPaso() {
        if (!cpu.hayPrograma() || cpu.haTerminado() || temporizador.isRunning()) {
            return;
        }
        ejecutarUnPaso();
    }

    /**
     * Nombre: alReiniciar
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: detiene antes la ejecucion automatica si estaba corriendo
     * Descripcion: vuelve al inicio del programa sin descargarlo de memoria.
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
     * Nombre: alLimpiar
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: detiene antes la ejecucion automatica si estaba corriendo
     * Descripcion: descarga el programa y deja la memoria de usuario, los
     *              registros, las tablas y la consola en blanco.
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
     * Nombre: alConfigurar
     * Entradas: tamanoMemoria, cantidad total de posiciones; limiteKernel,
     *           primera direccion de la zona de usuario; velocidadMs,
     *           milisegundos entre instrucciones
     * Salidas: ninguna
     * Restricciones: si los valores no son coherentes se informa el error y no
     *                se cambia nada; descarga siempre el programa actual
     * Descripcion: aplica una nueva configuracion. El programa se descarga
     *              porque redimensionar la memoria invalida las direcciones ya
     *              asignadas.
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

    /**
     * Nombre: alTicDelTemporizador
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: se ejecuta en el hilo de despacho de eventos
     * Descripcion: cada disparo del temporizador ejecuta una instruccion y, si
     *              ya no quedan, detiene la ejecucion automatica.
     */
    private void alTicDelTemporizador() {
        if (!ejecutarUnPaso()) {
            detener();
        }
    }

    /**
     * Nombre: ejecutarUnPaso
     * Entradas: ninguna
     * Salidas: true si queda alguna instruccion por ejecutar
     * Restricciones: atrapa DesbordamientoException, de modo que el error no
     *                se propaga hacia Swing
     * Descripcion: ejecuta una instruccion y atiende los dos finales posibles:
     *              que el programa termine normalmente o que se detenga por
     *              desbordamiento. En el segundo caso el procesador ya dejo el
     *              proceso en BLOQUEADO_ERROR y aviso a los observadores, asi
     *              que la pantalla ya refleja el estado y aqui solo falta
     *              informar al usuario.
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
     * Nombre: detener
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: no hace nada si el temporizador no estaba corriendo
     * Descripcion: detiene la ejecucion automatica y refresca la vista. El
     *              refresco final no es opcional: mientras el temporizador
     *              corre, el procesador notifica a los observadores antes de
     *              que este metodo lo detenga, de modo que esas notificaciones
     *              ven todavia isRunning() en true y dejan los botones
     *              deshabilitados. Sin este ultimo refresco la ventana se
     *              queda bloqueada al terminar el programa.
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

    /**
     * Nombre: alCambiarEstado
     * Entradas: procesador, el que cambio de estado; fase, momento del ciclo
     * Salidas: ninguna
     * Restricciones: se invoca desde el hilo que ejecuta la instruccion
     * Descripcion: el procesador avisa que algo cambio y el controlador se
     *              limita a refrescar la vista completa. No distingue la fase
     *              porque el refresco es el mismo en todas.
     */
    @Override
    public void alCambiarEstado(Procesador procesador, Fase fase) {
        actualizarVista();
    }

    /**
     * Nombre: actualizarVista
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: ninguna
     * Descripcion: vuelca el estado actual del procesador sobre la ventana:
     *              resaltado, tabla de memoria, panel del BCP, barra de
     *              contexto, ocupacion de memoria y estado de los botones.
     *              Antes actualiza los renderers, que necesitan saber que fila
     *              y que direccion destacar.
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

    /**
     * Nombre: textoDelEstado
     * Entradas: ninguna
     * Salidas: el estado del proceso en texto
     * Restricciones: ninguna
     * Descripcion: devuelve el nombre del estado, o la leyenda SIN PROGRAMA
     *              cuando no hay nada cargado, que no es un estado del proceso
     *              sino la ausencia de proceso.
     */
    private String textoDelEstado() {
        return cpu.hayPrograma() ? cpu.getEstado().name() : "SIN PROGRAMA";
    }

    // ------------------------------------------------------------------
    // Acceso para la ventana
    // ------------------------------------------------------------------

    /**
     * Nombre: getModeloInstrucciones
     * Entradas: ninguna
     * Salidas: el modelo de la tabla de instrucciones
     * Restricciones: ninguna
     * Descripcion: la ventana se lo asigna a su tabla al construirse.
     */
    public ModeloTablaInstrucciones getModeloInstrucciones() {
        return modeloInstrucciones;
    }

    /**
     * Nombre: getModeloMemoria
     * Entradas: ninguna
     * Salidas: el modelo de la tabla de memoria
     * Restricciones: ninguna
     * Descripcion: la ventana se lo asigna a su tabla al construirse.
     */
    public ModeloTablaMemoria getModeloMemoria() {
        return modeloMemoria;
    }

    /**
     * Nombre: getRenderInstrucciones
     * Entradas: ninguna
     * Salidas: el renderer que resalta la instruccion actual
     * Restricciones: ninguna
     * Descripcion: la ventana se lo asigna a su tabla al construirse.
     */
    public RenderInstruccionActual getRenderInstrucciones() {
        return renderInstrucciones;
    }

    /**
     * Nombre: getRenderMemoria
     * Entradas: ninguna
     * Salidas: el renderer que colorea las zonas de memoria
     * Restricciones: ninguna
     * Descripcion: la ventana se lo asigna a su tabla al construirse.
     */
    public RenderZonaMemoria getRenderMemoria() {
        return renderMemoria;
    }

    /**
     * Nombre: obtenerEstadisticas
     * Entradas: ninguna
     * Salidas: la contabilidad de la ejecucion
     * Restricciones: ninguna
     * Descripcion: la consulta el dialogo de estadisticas.
     */
    public Estadisticas obtenerEstadisticas() {
        return cpu.getEstadisticas();
    }

    /**
     * Nombre: getProcesador
     * Entradas: ninguna
     * Salidas: el procesador que el controlador coordina
     * Restricciones: ninguna
     * Descripcion: lo necesitan los dialogos para leer la memoria y el BCP,
     *              y las pruebas para verificar el resultado de la ejecucion.
     */
    public Procesador getProcesador() {
        return cpu;
    }

    /**
     * Nombre: getVelocidadMs
     * Entradas: ninguna
     * Salidas: milisegundos configurados entre instrucciones
     * Restricciones: ninguna
     * Descripcion: el dialogo de configuracion lo usa para mostrar el valor
     *              actual al abrirse.
     */
    public int getVelocidadMs() {
        return temporizador.getDelay();
    }

    /**
     * Nombre: inicializarVista
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: debe llamarse una vez, al final del constructor de la
     *                ventana, cuando sus componentes ya existen
     * Descripcion: deja la ventana en su estado inicial y escribe en la
     *              consola la configuracion de memoria con la que arranco.
     */
    public void inicializarVista() {
        actualizarVista();
        vista.escribirEnConsola("Mini PC listo. Memoria de "
                + cpu.getMemoria().getTamano() + " posiciones, kernel de 0 a "
                + (cpu.getMemoria().getLimiteKernel() - 1) + ".");
    }
}
