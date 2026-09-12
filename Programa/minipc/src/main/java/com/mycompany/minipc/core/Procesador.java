package com.mycompany.minipc.core;

import com.mycompany.minipc.excepciones.DesbordamientoException;
import com.mycompany.minipc.excepciones.MemoriaInsuficienteException;
import com.mycompany.minipc.isa.Instruccion;
import com.mycompany.minipc.isa.OpCode;
import com.mycompany.minipc.isa.RegistroID;
import com.mycompany.minipc.util.BinUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * El procesador del Mini PC.
 *
 * Implementa el ciclo de instruccion de la figura 1.2 de Stallings: el
 * procesador repite indefinidamente traer la instruccion que apunta el PC
 * (etapa fetch) e interpretarla y ejecutarla (etapa execute).
 *
 * La aritmetica se resuelve sobre enteros decimales de Java. El binario
 * existe solo en tres momentos: al ensamblar, al decodificar la palabra
 * leida de memoria, y al mostrar los valores en pantalla.
 */
public class Procesador {

    private final Memoria memoria;
    private final BancoRegistros registros;
    private final Estadisticas estadisticas;
    private final List<ObservadorCPU> observadores;

    private BCP bcp;

    /** Program Counter: direccion de la proxima instruccion. */
    private int pc;

    /** Instruction Register: la instruccion que se esta ejecutando. */
    private int ir;

    /** Accumulator: almacenamiento temporal donde ocurre la aritmetica. */
    private int ac;

    /** Texto legible de la instruccion en el IR, para mostrar en el BCP. */
    private String irTexto;

    private int direccionBase;
    private int direccionFin;
    private int cantidadInstrucciones;
    private int instruccionesEjecutadas;
    private int ciclosReloj;

    private EstadoProceso estado;
    private int siguientePid;

    /**
     * Crea un procesador con su memoria y sus registros, sin programa.
     */
    public Procesador() {
        this.memoria = new Memoria();
        this.registros = new BancoRegistros();
        this.estadisticas = new Estadisticas();
        this.observadores = new ArrayList<>();
        this.siguientePid = 1;
        this.irTexto = "";
        this.estado = EstadoProceso.NUEVO;
    }

    /**
     * Carga un programa en la zona de usuario y deja el procesador listo
     * para ejecutarlo desde la primera instruccion.
     *
     * @param programa       instrucciones ya ensambladas
     * @param nombreArchivo  nombre del archivo de origen, para el BCP
     * @throws MemoriaInsuficienteException si el programa no cabe en memoria
     */
    public void cargar(List<Instruccion> programa, String nombreArchivo)
            throws MemoriaInsuficienteException {
        direccionBase = memoria.cargarPrograma(programa);
        cantidadInstrucciones = programa.size();
        direccionFin = direccionBase + cantidadInstrucciones - 1;

        registros.reset();
        estadisticas.reset();
        estadisticas.setPosicionesUsadas(memoria.getPosicionesUsadas());

        pc = direccionBase;
        ir = 0;
        ac = 0;
        irTexto = "";
        instruccionesEjecutadas = 0;
        ciclosReloj = 0;

        bcp = new BCP(siguientePid++, nombreArchivo);
        estado = EstadoProceso.LISTO;
        bcp.actualizarDesde(this);

        notificar(Fase.CARGA);
    }

    /**
     * Ejecuta una sola instruccion: un ciclo fetch mas execute completo.
     *
     * @return true si queda al menos una instruccion por ejecutar
     * @throws DesbordamientoException si el resultado aritmetico no cabe
     *         en el formato de 8 bits; el proceso queda en BLOQUEADO_ERROR
     */
    public boolean paso() {
        if (!hayPrograma() || estado.esFinal()) {
            return false;
        }
        estado = EstadoProceso.EJECUCION;

        // ---------- ETAPA FETCH ----------
        CeldaMemoria celda = memoria.leerComoUsuario(pc);
        ir = celda.getPalabra();
        irTexto = celda.getEtiqueta();
        pc++;
        ciclosReloj++;
        estadisticas.registrarLectura();
        bcp.actualizarDesde(this);
        notificar(Fase.FETCH);

        // ---------- ETAPA DECODE ----------
        OpCode opcode = OpCode.desdeCodigo((ir >>> 12) & 0xF);
        RegistroID registro = RegistroID.desdeCodigo((ir >>> 8) & 0xF);
        int operando = BinUtil.aEntero(ir & 0xFF);

        // ---------- ETAPA EXECUTE ----------
        try {
            ejecutar(opcode, registro, operando);
        } catch (DesbordamientoException e) {
            // El proceso no puede continuar, pero la interfaz tiene que
            // poder mostrar en que estado quedo antes de ver el error.
            estado = EstadoProceso.BLOQUEADO_ERROR;
            ciclosReloj++;
            bcp.actualizarDesde(this);
            notificar(Fase.EXECUTE);
            throw e;
        }

        ciclosReloj++;
        instruccionesEjecutadas++;
        estadisticas.registrar(opcode);

        if (pc > direccionFin) {
            estado = EstadoProceso.TERMINADO;
        }

        bcp.actualizarDesde(this);
        notificar(Fase.EXECUTE);

        return !haTerminado();
    }

    private void ejecutar(OpCode opcode, RegistroID registro, int operando) {
        switch (opcode) {
            case MOV:
                registros.escribir(registro, operando);
                estadisticas.registrarEscritura();
                break;
            case LOAD:
                ac = registros.leer(registro);
                break;
            case STORE:
                registros.escribir(registro, ac);
                estadisticas.registrarEscritura();
                break;
            case ADD:
                ac = validarRango(ac + registros.leer(registro), opcode);
                break;
            case SUB:
                ac = validarRango(ac - registros.leer(registro), opcode);
                break;
            default:
                throw new IllegalStateException("Operacion no implementada: " + opcode);
        }
    }

    /**
     * Comprueba que un resultado aritmetico quepa en el formato de 8 bits.
     *
     * Se eligio detener el proceso en lugar de saturar en el limite: es
     * mas honesto con el formato y permite mostrar el manejo del error.
     */
    private int validarRango(int resultado, OpCode opcode) {
        if (!BinUtil.esRepresentable(resultado)) {
            throw new DesbordamientoException("Desbordamiento en " + opcode + ": el resultado "
                    + resultado + " esta fuera del rango representable ("
                    + BinUtil.VALOR_MINIMO + " a " + BinUtil.VALOR_MAXIMO + ")");
        }
        return resultado;
    }

    /**
     * Vuelve al inicio del programa sin descargarlo de memoria.
     */
    public void reset() {
        if (!hayPrograma()) {
            return;
        }
        registros.reset();
        estadisticas.reset();
        estadisticas.setPosicionesUsadas(memoria.getPosicionesUsadas());

        pc = direccionBase;
        ir = 0;
        ac = 0;
        irTexto = "";
        instruccionesEjecutadas = 0;
        ciclosReloj = 0;
        estado = EstadoProceso.LISTO;

        bcp.actualizarDesde(this);
        notificar(Fase.REINICIO);
    }

    /**
     * Descarga el programa y deja el procesador como recien arrancado.
     */
    public void limpiar() {
        memoria.limpiarZonaUsuario();
        registros.reset();
        estadisticas.reset();

        pc = 0;
        ir = 0;
        ac = 0;
        irTexto = "";
        direccionBase = 0;
        direccionFin = -1;
        cantidadInstrucciones = 0;
        instruccionesEjecutadas = 0;
        ciclosReloj = 0;
        bcp = null;
        estado = EstadoProceso.NUEVO;

        notificar(Fase.REINICIO);
    }

    /**
     * Cambia la configuracion de la memoria. Descarga el programa actual.
     *
     * @param tamano       cantidad total de posiciones
     * @param limiteKernel primera direccion de la zona de usuario
     */
    public void configurarMemoria(int tamano, int limiteKernel) {
        memoria.redimensionar(tamano, limiteKernel);
        limpiar();
    }

    /**
     * @return true si el programa llego al final o quedo bloqueado
     */
    public boolean haTerminado() {
        return estado.esFinal();
    }

    /**
     * @return true si hay un programa cargado en memoria
     */
    public boolean hayPrograma() {
        return cantidadInstrucciones > 0 && bcp != null;
    }

    /**
     * Registra un interesado en los cambios del procesador.
     *
     * @param observador quien quiere enterarse
     */
    public void agregarObservador(ObservadorCPU observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void quitarObservador(ObservadorCPU observador) {
        observadores.remove(observador);
    }

    private void notificar(Fase fase) {
        for (ObservadorCPU observador : observadores) {
            observador.alCambiarEstado(this, fase);
        }
    }

    public Memoria getMemoria() {
        return memoria;
    }

    public BancoRegistros getRegistros() {
        return registros;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }

    public BCP getBcp() {
        return bcp;
    }

    public EstadoProceso getEstado() {
        return estado;
    }

    public int getPc() {
        return pc;
    }

    public int getIr() {
        return ir;
    }

    public String getIrTexto() {
        return irTexto;
    }

    public int getAc() {
        return ac;
    }

    public int getDireccionBase() {
        return direccionBase;
    }

    /**
     * @return cuantas posiciones de memoria ocupa el programa cargado
     */
    public int getLimite() {
        return cantidadInstrucciones;
    }

    public int getInstruccionesEjecutadas() {
        return instruccionesEjecutadas;
    }

    public int getCiclosReloj() {
        return ciclosReloj;
    }

    /**
     * Indica que posicion del programa esta por ejecutarse, contando desde
     * cero. Lo usa la interfaz para resaltar la fila correspondiente.
     *
     * @return indice dentro del programa, o -1 si ya no queda ninguna
     */
    public int getIndiceInstruccionActual() {
        if (!hayPrograma() || pc > direccionFin) {
            return -1;
        }
        return pc - direccionBase;
    }
}
