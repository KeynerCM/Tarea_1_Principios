package com.mycompany.minipc.core;

import com.mycompany.minipc.isa.RegistroID;
import com.mycompany.minipc.util.BinUtil;

/**
 * Nombre: Registro
 * Entradas: la identidad del registro que representa
 * Salidas: no aplica
 * Restricciones: el valor que almacena debe mantenerse siempre entre -127 y
 *                127, que es lo que admite el formato de ocho bits
 * Descripcion: un registro de proposito general del Mini PC. El valor se
 *              guarda como entero decimal de Java. La representacion en
 *              signo-magnitud solo se genera cuando hay que mostrarla, tal
 *              como se aclaro en clase: la aritmetica se resuelve en decimal
 *              y el binario aparece unicamente al codificar y al desplegar.
 */
public class Registro {

    private final RegistroID id;
    private int valor;

    /**
     * Nombre: Registro
     * Entradas: id, identidad del registro
     * Salidas: el registro construido, con valor cero
     * Restricciones: la identidad no cambia durante la vida del objeto
     * Descripcion: crea el registro en su estado inicial, que es cero, tal
     *              como queda un procesador recien encendido.
     */
    public Registro(RegistroID id) {
        this.id = id;
        this.valor = 0;
    }

    /**
     * Nombre: getId
     * Entradas: ninguna
     * Salidas: la identidad del registro
     * Restricciones: ninguna
     * Descripcion: permite saber de que registro se trata al recorrer el
     *              banco completo.
     */
    public RegistroID getId() {
        return id;
    }

    /**
     * Nombre: getValor
     * Entradas: ninguna
     * Salidas: el contenido actual como entero de Java
     * Restricciones: ninguna
     * Descripcion: acceso de solo lectura al valor almacenado.
     */
    public int getValor() {
        return valor;
    }

    /**
     * Nombre: setValor
     * Entradas: valor, entero entre -127 y 127
     * Salidas: ninguna
     * Restricciones: si el valor no es representable lanza
     *                DesbordamientoException y el registro queda intacto
     * Descripcion: asigna un valor validando antes que quepa en el formato
     *              de ocho bits. La validacion se delega en BinUtil, de modo
     *              que la regla del rango vive en un solo lugar.
     */
    public void setValor(int valor) {
        BinUtil.aSignoMagnitud(valor);
        this.valor = valor;
    }

    /**
     * Nombre: getBinario
     * Entradas: ninguna
     * Salidas: el contenido en signo-magnitud de ocho bits
     * Restricciones: ninguna, el valor almacenado siempre es representable
     * Descripcion: devuelve por ejemplo "10001000" para un registro que vale
     *              -8. Se usa solo para mostrar en pantalla.
     */
    public String getBinario() {
        return BinUtil.aBinarioEntero(valor);
    }

    /**
     * Nombre: reset
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: ninguna
     * Descripcion: devuelve el registro a cero, sin alterar su identidad.
     */
    public void reset() {
        this.valor = 0;
    }

    /**
     * Nombre: toString
     * Entradas: ninguna
     * Salidas: representacion legible del registro, por ejemplo "AX=5"
     * Restricciones: ninguna
     * Descripcion: pensado para depuracion y para los mensajes de las pruebas.
     */
    @Override
    public String toString() {
        return id + "=" + valor;
    }
}
