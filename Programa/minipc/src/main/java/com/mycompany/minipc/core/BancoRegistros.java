package com.mycompany.minipc.core;

import com.mycompany.minipc.isa.RegistroID;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Los cuatro registros de proposito general del Mini PC.
 *
 * Se usa un EnumMap porque la clave es un enum: internamente es un arreglo
 * indexado por el ordinal, asi que el acceso es directo y el orden de
 * recorrido es siempre AX, BX, CX, DX, que es como conviene mostrarlos
 * en la interfaz.
 */
public class BancoRegistros {

    private final Map<RegistroID, Registro> registros;

    /**
     * Crea los cuatro registros, todos en cero.
     */
    public BancoRegistros() {
        registros = new EnumMap<>(RegistroID.class);
        for (RegistroID id : RegistroID.values()) {
            registros.put(id, new Registro(id));
        }
    }

    /**
     * @param id registro a consultar
     * @return el valor actual
     */
    public int leer(RegistroID id) {
        return obtener(id).getValor();
    }

    /**
     * @param id    registro a modificar
     * @param valor entero entre -127 y 127
     * @throws com.mycompany.minipc.excepciones.DesbordamientoException
     *         si el valor no es representable
     */
    public void escribir(RegistroID id, int valor) {
        obtener(id).setValor(valor);
    }

    /**
     * @param id registro a consultar
     * @return el objeto registro completo
     */
    public Registro obtener(RegistroID id) {
        Registro registro = registros.get(id);
        if (registro == null) {
            throw new IllegalArgumentException("Registro inexistente: " + id);
        }
        return registro;
    }

    /**
     * Pone los cuatro registros en cero.
     */
    public void reset() {
        for (Registro registro : registros.values()) {
            registro.reset();
        }
    }

    /**
     * @return los cuatro registros en orden AX, BX, CX, DX
     */
    public Collection<Registro> todos() {
        return Collections.unmodifiableCollection(registros.values());
    }

    /**
     * Copia el contenido actual, para guardarlo en el BCP.
     *
     * @return un mapa independiente con el valor de cada registro
     */
    public Map<RegistroID, Integer> instantanea() {
        Map<RegistroID, Integer> copia = new EnumMap<>(RegistroID.class);
        for (Map.Entry<RegistroID, Registro> entrada : registros.entrySet()) {
            copia.put(entrada.getKey(), entrada.getValue().getValor());
        }
        return copia;
    }
}
