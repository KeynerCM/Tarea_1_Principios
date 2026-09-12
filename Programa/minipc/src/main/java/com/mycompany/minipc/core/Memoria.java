package com.mycompany.minipc.core;

import com.mycompany.minipc.excepciones.MemoriaInsuficienteException;
import com.mycompany.minipc.isa.Instruccion;

import java.util.List;

/**
 * Memoria principal del Mini PC, dividida en zona de kernel y zona de usuario.
 *
 * El mapa es el del enunciado:
 *
 *   0 .. limiteKernel-1    zona del sistema operativo
 *   limiteKernel .. tamano-1   zona del usuario, donde se carga el programa
 *
 * Cada posicion guarda una palabra de 16 bits, de modo que una linea de
 * programa ocupa exactamente una celda.
 */
public class Memoria {

    /** Tamano minimo exigido por el enunciado. */
    public static final int TAMANO_MINIMO = 128;

    /** Tamano con el que arranca la aplicacion. */
    public static final int TAMANO_POR_DEFECTO = 256;

    /** Limite de kernel con el que arranca la aplicacion. */
    public static final int LIMITE_KERNEL_POR_DEFECTO = 64;

    /** Minimo de posiciones reservadas al sistema operativo. */
    public static final int LIMITE_KERNEL_MINIMO = 16;

    private CeldaMemoria[] celdas;
    private int tamano;
    private int limiteKernel;

    /**
     * Crea la memoria con la configuracion por defecto: 256 posiciones y
     * kernel de 0 a 63.
     */
    public Memoria() {
        redimensionar(TAMANO_POR_DEFECTO, LIMITE_KERNEL_POR_DEFECTO);
    }

    /**
     * Cambia el tamano de la memoria y el limite entre zonas. Descarta
     * todo el contenido anterior.
     *
     * @param tamano       cantidad total de posiciones, al menos 128
     * @param limiteKernel primera direccion de la zona de usuario
     * @throws IllegalArgumentException si los valores no son coherentes
     */
    public final void redimensionar(int tamano, int limiteKernel) {
        if (tamano < TAMANO_MINIMO) {
            throw new IllegalArgumentException(
                    "El tamano de memoria debe ser de al menos " + TAMANO_MINIMO
                    + ", se recibio " + tamano);
        }
        if (limiteKernel < LIMITE_KERNEL_MINIMO) {
            throw new IllegalArgumentException(
                    "El limite de kernel debe ser de al menos " + LIMITE_KERNEL_MINIMO
                    + ", se recibio " + limiteKernel);
        }
        if (limiteKernel >= tamano) {
            throw new IllegalArgumentException("El limite de kernel (" + limiteKernel
                    + ") debe ser menor que el tamano total (" + tamano + ")");
        }

        this.tamano = tamano;
        this.limiteKernel = limiteKernel;
        this.celdas = new CeldaMemoria[tamano];
        for (int i = 0; i < tamano; i++) {
            celdas[i] = new CeldaMemoria();
            if (i < limiteKernel) {
                celdas[i].escribir(0, CeldaMemoria.Tipo.RESERVADA_KERNEL, "");
            }
        }
    }

    public int getTamano() {
        return tamano;
    }

    public int getLimiteKernel() {
        return limiteKernel;
    }

    /**
     * @return cuantas posiciones tiene la zona de usuario
     */
    public int getEspacioUsuario() {
        return tamano - limiteKernel;
    }

    /**
     * @param direccion posicion a evaluar
     * @return true si pertenece a la zona del sistema operativo
     */
    public boolean esDireccionKernel(int direccion) {
        return direccion >= 0 && direccion < limiteKernel;
    }

    /**
     * Comprueba que el programa quepa en la zona de usuario.
     *
     * @param lineasRequeridas cantidad de posiciones que ocupa el programa
     * @throws MemoriaInsuficienteException si no hay espacio suficiente
     */
    public void validarEspacio(int lineasRequeridas) throws MemoriaInsuficienteException {
        int disponibles = getEspacioUsuario();
        if (lineasRequeridas > disponibles) {
            throw new MemoriaInsuficienteException(lineasRequeridas, disponibles);
        }
    }

    /**
     * Carga un programa al inicio de la zona de usuario.
     *
     * La operacion es atomica: primero valida el espacio y solo entonces
     * limpia y escribe. Si no cabe, la memoria queda intacta.
     *
     * @param programa instrucciones ya ensambladas
     * @return la direccion base donde quedo cargado
     * @throws MemoriaInsuficienteException si el programa no cabe
     */
    public int cargarPrograma(List<Instruccion> programa) throws MemoriaInsuficienteException {
        validarEspacio(programa.size());
        limpiarZonaUsuario();

        int base = limiteKernel;
        for (int i = 0; i < programa.size(); i++) {
            Instruccion instruccion = programa.get(i);
            celdas[base + i].escribir(instruccion.aPalabra(),
                    CeldaMemoria.Tipo.INSTRUCCION, instruccion.getTextoFuente());
        }
        return base;
    }

    /**
     * Lee una posicion cualquiera, sin restriccion de zona. Lo usa la
     * interfaz para mostrar la tabla de memoria completa.
     *
     * @param direccion posicion a leer
     * @return la celda correspondiente
     * @throws IndexOutOfBoundsException si la direccion no existe
     */
    public CeldaMemoria leer(int direccion) {
        validarDireccion(direccion);
        return celdas[direccion];
    }

    /**
     * Lee una posicion en nombre del proceso de usuario.
     *
     * Ademas de validar el rango, rechaza el acceso a la zona del kernel.
     * Es el equivalente didactico de una violacion de segmento: el proceso
     * no puede leer la memoria del sistema operativo.
     *
     * @param direccion posicion a leer
     * @return la celda correspondiente
     * @throws IndexOutOfBoundsException si la direccion no existe
     * @throws IllegalArgumentException  si la direccion es del kernel
     */
    public CeldaMemoria leerComoUsuario(int direccion) {
        validarDireccion(direccion);
        if (esDireccionKernel(direccion)) {
            throw new IllegalArgumentException("Acceso denegado: la direccion " + direccion
                    + " pertenece a la zona de kernel (0 a " + (limiteKernel - 1) + ")");
        }
        return celdas[direccion];
    }

    /**
     * Escribe en una posicion cualquiera.
     *
     * @param direccion posicion a escribir
     * @param palabra   los 16 bits a guardar
     * @param tipo      para que queda destinada la celda
     * @param etiqueta  texto legible a mostrar
     * @throws IndexOutOfBoundsException si la direccion no existe
     */
    public void escribir(int direccion, int palabra, CeldaMemoria.Tipo tipo, String etiqueta) {
        validarDireccion(direccion);
        celdas[direccion].escribir(palabra, tipo, etiqueta);
    }

    /**
     * Deja libre toda la zona de usuario. No toca la zona del kernel.
     */
    public final void limpiarZonaUsuario() {
        for (int i = limiteKernel; i < tamano; i++) {
            celdas[i].limpiar();
        }
    }

    /**
     * @return cuantas posiciones de la zona de usuario estan ocupadas
     */
    public int getPosicionesUsadas() {
        int usadas = 0;
        for (int i = limiteKernel; i < tamano; i++) {
            if (!celdas[i].estaLibre()) {
                usadas++;
            }
        }
        return usadas;
    }

    /**
     * @return porcentaje de la zona de usuario ocupado, de 0 a 100
     */
    public int getPorcentajeUso() {
        int disponibles = getEspacioUsuario();
        return disponibles == 0 ? 0 : (getPosicionesUsadas() * 100) / disponibles;
    }

    private void validarDireccion(int direccion) {
        if (direccion < 0 || direccion >= tamano) {
            throw new IndexOutOfBoundsException("Direccion fuera de la memoria: " + direccion
                    + ", el rango valido es 0 a " + (tamano - 1));
        }
    }
}
