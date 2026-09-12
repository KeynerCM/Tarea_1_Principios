package com.mycompany.minipc.excepciones;

import java.util.Collections;
import java.util.List;

/**
 * Reune los errores de sintaxis encontrados al ensamblar un archivo.
 *
 * El ensamblador no se detiene en el primer error: recorre el archivo
 * completo, junta todos los problemas y los reporta de una sola vez, para
 * que el usuario los corrija en una pasada en lugar de descubrirlos uno
 * por uno. Por eso la excepcion transporta una lista y no un solo mensaje.
 */
public class SintaxisException extends Exception {

    private static final long serialVersionUID = 1L;

    private final List<String> errores;

    /**
     * @param errores mensajes de error, uno por problema encontrado
     */
    public SintaxisException(List<String> errores) {
        super(String.join(System.lineSeparator(), errores));
        this.errores = List.copyOf(errores);
    }

    /**
     * @param error unico mensaje de error
     */
    public SintaxisException(String error) {
        super(error);
        this.errores = Collections.singletonList(error);
    }

    /**
     * @return los mensajes de error, en el orden en que aparecen en el archivo
     */
    public List<String> getErrores() {
        return errores;
    }

    /**
     * @return cuantos errores se encontraron
     */
    public int cantidad() {
        return errores.size();
    }
}
