package com.mycompany.minipc.core;

import com.mycompany.minipc.isa.RegistroID;
import com.mycompany.minipc.util.BinUtil;

/**
 * Un registro de proposito general del Mini PC.
 *
 * El valor se guarda como entero decimal de Java. La representacion en
 * signo-magnitud solo se genera cuando hay que mostrarla, tal como se
 * aclaro en clase: la aritmetica se resuelve en decimal y el binario
 * aparece unicamente al codificar y al desplegar.
 */
public class Registro {

    private final RegistroID id;
    private int valor;

    /**
     * Crea el registro en cero.
     *
     * @param id identidad del registro
     */
    public Registro(RegistroID id) {
        this.id = id;
        this.valor = 0;
    }

    public RegistroID getId() {
        return id;
    }

    public int getValor() {
        return valor;
    }

    /**
     * Asigna un valor, validando que quepa en el formato de 8 bits.
     *
     * @param valor entero entre -127 y 127
     * @throws com.mycompany.minipc.excepciones.DesbordamientoException
     *         si el valor no es representable
     */
    public void setValor(int valor) {
        BinUtil.aSignoMagnitud(valor);
        this.valor = valor;
    }

    /**
     * @return el contenido en signo-magnitud de 8 bits, por ejemplo "10001000"
     */
    public String getBinario() {
        return BinUtil.aBinarioEntero(valor);
    }

    /**
     * Devuelve el registro a su estado inicial.
     */
    public void reset() {
        this.valor = 0;
    }

    @Override
    public String toString() {
        return id + "=" + valor;
    }
}
