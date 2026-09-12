package com.mycompany.minipc.core;

import com.mycompany.minipc.isa.RegistroID;
import com.mycompany.minipc.util.BinUtil;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

/**
 * Bloque de Control de Proceso.
 *
 * Reune todo lo que el sistema operativo necesitaria para suspender el
 * proceso y reanudarlo mas tarde exactamente donde quedo: su identidad,
 * su estado, el contexto del procesador, donde vive en memoria y su
 * contabilidad.
 *
 * En este simulador solo hay un proceso a la vez, asi que el BCP no se
 * guarda en una cola; pero se mantiene actualizado despues de cada
 * instruccion para que la interfaz lo muestre paso a paso.
 */
public class BCP {

    private int pid;
    private String nombrePrograma;
    private EstadoProceso estado;

    // Contexto del procesador
    private int pc;
    private int ir;
    private String irTexto;
    private int ac;
    private final Map<RegistroID, Integer> snapshotRegistros;

    // Informacion de memoria
    private int direccionBase;
    private int limite;

    // Contabilidad
    private int instruccionesEjecutadas;
    private int ciclosReloj;
    private final LocalDateTime horaCreacion;

    /**
     * Crea el bloque de control de un proceso recien admitido.
     *
     * @param pid             identificador del proceso
     * @param nombrePrograma  nombre del archivo cargado
     */
    public BCP(int pid, String nombrePrograma) {
        this.pid = pid;
        this.nombrePrograma = nombrePrograma;
        this.estado = EstadoProceso.NUEVO;
        this.snapshotRegistros = new EnumMap<>(RegistroID.class);
        for (RegistroID id : RegistroID.values()) {
            snapshotRegistros.put(id, 0);
        }
        this.irTexto = "";
        this.horaCreacion = LocalDateTime.now();
    }

    /**
     * Copia el contexto actual del procesador dentro del bloque.
     *
     * Es el equivalente al guardado de contexto que hace el sistema
     * operativo en un cambio de proceso.
     *
     * @param cpu procesador del que se toma el contexto
     */
    public void actualizarDesde(Procesador cpu) {
        this.estado = cpu.getEstado();
        this.pc = cpu.getPc();
        this.ir = cpu.getIr();
        this.irTexto = cpu.getIrTexto();
        this.ac = cpu.getAc();
        this.snapshotRegistros.putAll(cpu.getRegistros().instantanea());
        this.direccionBase = cpu.getDireccionBase();
        this.limite = cpu.getLimite();
        this.instruccionesEjecutadas = cpu.getInstruccionesEjecutadas();
        this.ciclosReloj = cpu.getCiclosReloj();
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getNombrePrograma() {
        return nombrePrograma;
    }

    public void setNombrePrograma(String nombrePrograma) {
        this.nombrePrograma = nombrePrograma;
    }

    public EstadoProceso getEstado() {
        return estado;
    }

    public void setEstado(EstadoProceso estado) {
        this.estado = estado;
    }

    public int getPc() {
        return pc;
    }

    public int getIr() {
        return ir;
    }

    /**
     * @return la instruccion del IR en binario, "0011 0001 00000101"
     */
    public String getIrBinario() {
        return BinUtil.aBinarioPalabra(ir);
    }

    /**
     * @return la instruccion del IR en texto legible, "MOV AX, 5"
     */
    public String getIrTexto() {
        return irTexto;
    }

    public int getAc() {
        return ac;
    }

    /**
     * @param id registro a consultar
     * @return el valor que tenia al momento de la ultima actualizacion
     */
    public int getRegistro(RegistroID id) {
        return snapshotRegistros.getOrDefault(id, 0);
    }

    /**
     * @return copia de solo lectura de los cuatro registros
     */
    public Map<RegistroID, Integer> getSnapshotRegistros() {
        return new EnumMap<>(snapshotRegistros);
    }

    public int getDireccionBase() {
        return direccionBase;
    }

    /**
     * @return cuantas posiciones de memoria ocupa el programa
     */
    public int getLimite() {
        return limite;
    }

    public int getInstruccionesEjecutadas() {
        return instruccionesEjecutadas;
    }

    public int getCiclosReloj() {
        return ciclosReloj;
    }

    public LocalDateTime getHoraCreacion() {
        return horaCreacion;
    }
}
