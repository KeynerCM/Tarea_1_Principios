package com.mycompany.minipc.core;

import com.mycompany.minipc.isa.OpCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Contabilidad de una ejecucion: cuantas instrucciones se ejecutaron, de
 * que tipo, cuantos accesos a memoria hubo y cuanto tardo.
 */
public class Estadisticas {

    private int totalInstrucciones;
    private final Map<OpCode, Integer> conteoPorOperacion;
    private int accesosLectura;
    private int accesosEscritura;
    private int posicionesUsadas;
    private long inicioMs;
    private long tiempoTotalMs;

    public Estadisticas() {
        conteoPorOperacion = new EnumMap<>(OpCode.class);
        reset();
    }

    /**
     * Deja la contabilidad en cero y arranca el cronometro.
     */
    public final void reset() {
        totalInstrucciones = 0;
        conteoPorOperacion.clear();
        for (OpCode op : OpCode.values()) {
            conteoPorOperacion.put(op, 0);
        }
        accesosLectura = 0;
        accesosEscritura = 0;
        posicionesUsadas = 0;
        inicioMs = System.currentTimeMillis();
        tiempoTotalMs = 0;
    }

    /**
     * Anota una instruccion ejecutada.
     *
     * @param opcode operacion que se acaba de ejecutar
     */
    public void registrar(OpCode opcode) {
        totalInstrucciones++;
        conteoPorOperacion.merge(opcode, 1, Integer::sum);
        tiempoTotalMs = System.currentTimeMillis() - inicioMs;
    }

    /**
     * Anota una lectura de memoria.
     */
    public void registrarLectura() {
        accesosLectura++;
    }

    /**
     * Anota una escritura en memoria.
     */
    public void registrarEscritura() {
        accesosEscritura++;
    }

    public int getTotalInstrucciones() {
        return totalInstrucciones;
    }

    /**
     * @param opcode operacion a consultar
     * @return cuantas veces se ejecuto
     */
    public int getConteo(OpCode opcode) {
        return conteoPorOperacion.getOrDefault(opcode, 0);
    }

    /**
     * @return el conteo por operacion, en el orden de declaracion del enum
     */
    public Map<OpCode, Integer> getConteoPorOperacion() {
        return Collections.unmodifiableMap(conteoPorOperacion);
    }

    public int getAccesosLectura() {
        return accesosLectura;
    }

    public int getAccesosEscritura() {
        return accesosEscritura;
    }

    public int getPosicionesUsadas() {
        return posicionesUsadas;
    }

    public void setPosicionesUsadas(int posicionesUsadas) {
        this.posicionesUsadas = posicionesUsadas;
    }

    public long getTiempoTotalMs() {
        return tiempoTotalMs;
    }
}
