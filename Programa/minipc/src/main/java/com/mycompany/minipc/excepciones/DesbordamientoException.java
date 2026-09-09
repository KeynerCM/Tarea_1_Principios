package com.mycompany.minipc.excepciones;

/**
 * Se lanza cuando un valor no cabe en el formato de entero de 8 bits en
 * signo-magnitud que usa el Mini PC, es decir, cuando queda fuera del
 * rango -127 a 127.
 *
 * Es una excepcion no verificada porque puede originarse en medio del
 * ciclo de ejecucion, dentro de Procesador.paso(), cuyo contrato no
 * declara excepciones. El controlador de la interfaz la atrapa y marca
 * el proceso como BLOQUEADO_ERROR.
 */
public class DesbordamientoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DesbordamientoException(String mensaje) {
        super(mensaje);
    }
}
