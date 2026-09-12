package com.mycompany.minipc.isa;

import com.mycompany.minipc.excepciones.SintaxisException;
import com.mycompany.minipc.util.BinUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Traduce el texto de un archivo .asm a instrucciones del Mini PC.
 *
 * Es un ensamblador de una sola pasada: el juego de instrucciones no
 * tiene saltos ni etiquetas, asi que no hace falta resolver referencias
 * hacia adelante.
 *
 * Recorre el archivo completo antes de fallar. Si hay errores, los junta
 * todos en una sola SintaxisException, para que la interfaz los muestre
 * de una vez y el usuario los corrija en una pasada.
 */
public class Ensamblador {

    /** Marca de comentario de una linea al estilo ensamblador. */
    private static final String COMENTARIO_PUNTO_COMA = ";";

    /** Marca de comentario de una linea al estilo Java. */
    private static final String COMENTARIO_BARRAS = "//";

    /**
     * Ensambla un archivo completo.
     *
     * Las lineas vacias y las de solo comentario se descartan: no ocupan
     * posicion en memoria.
     *
     * @param lineas contenido del archivo, una entrada por linea
     * @return las instrucciones traducidas, en orden
     * @throws SintaxisException si alguna linea tiene errores
     */
    public List<Instruccion> ensamblar(List<String> lineas) throws SintaxisException {
        List<Instruccion> programa = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        for (int i = 0; i < lineas.size(); i++) {
            int numeroLinea = i + 1;
            String util = quitarComentario(lineas.get(i)).trim();
            if (util.isEmpty()) {
                continue;
            }
            try {
                programa.add(ensamblarLinea(util, numeroLinea));
            } catch (SintaxisException e) {
                errores.addAll(e.getErrores());
            }
        }

        if (!errores.isEmpty()) {
            throw new SintaxisException(errores);
        }
        if (programa.isEmpty()) {
            throw new SintaxisException("El archivo no contiene ninguna instruccion");
        }
        return programa;
    }

    /**
     * Traduce una sola linea, ya limpia de comentarios y espacios.
     *
     * @param linea       texto de la linea
     * @param numeroLinea numero de linea dentro del archivo, desde 1
     * @return la instruccion correspondiente
     * @throws SintaxisException si la linea no respeta la sintaxis
     */
    private Instruccion ensamblarLinea(String linea, int numeroLinea) throws SintaxisException {
        // La coma es separador opcional: MOV AX, 5 y MOV AX 5 son equivalentes.
        String[] tokens = linea.replace(',', ' ').trim().split("\s+");

        OpCode opcode = leerOpcode(tokens[0], numeroLinea);
        RegistroID registro = leerRegistro(tokens, numeroLinea, opcode);
        int operando = leerOperando(tokens, numeroLinea, opcode);

        return new Instruccion(opcode, registro, operando, linea, numeroLinea);
    }

    private OpCode leerOpcode(String token, int numeroLinea) throws SintaxisException {
        try {
            return OpCode.desdeMnemonico(token);
        } catch (IllegalArgumentException e) {
            throw new SintaxisException(
                    "Linea " + numeroLinea + ": operacion desconocida \"" + token + "\"");
        }
    }

    private RegistroID leerRegistro(String[] tokens, int numeroLinea, OpCode opcode)
            throws SintaxisException {
        if (tokens.length < 2) {
            throw new SintaxisException(
                    "Linea " + numeroLinea + ": falta el registro para " + opcode);
        }
        try {
            return RegistroID.desdeNombre(tokens[1]);
        } catch (IllegalArgumentException e) {
            throw new SintaxisException(
                    "Linea " + numeroLinea + ": registro inexistente \"" + tokens[1] + "\"");
        }
    }

    private int leerOperando(String[] tokens, int numeroLinea, OpCode opcode)
            throws SintaxisException {
        int esperados = opcode.requiereInmediato() ? 3 : 2;

        if (tokens.length > esperados) {
            throw new SintaxisException("Linea " + numeroLinea + ": sobran operandos para "
                    + opcode + ", se esperaban " + esperados + " elementos");
        }
        if (!opcode.requiereInmediato()) {
            return 0;
        }
        if (tokens.length < 3) {
            throw new SintaxisException(
                    "Linea " + numeroLinea + ": falta el valor inmediato para " + opcode);
        }

        int valor;
        try {
            valor = Integer.parseInt(tokens[2]);
        } catch (NumberFormatException e) {
            throw new SintaxisException(
                    "Linea " + numeroLinea + ": valor no numerico \"" + tokens[2] + "\"");
        }
        if (!BinUtil.esRepresentable(valor)) {
            throw new SintaxisException("Linea " + numeroLinea + ": el valor " + valor
                    + " esta fuera del rango " + BinUtil.VALOR_MINIMO
                    + " a " + BinUtil.VALOR_MAXIMO);
        }
        return valor;
    }

    /**
     * Recorta la linea en la primera marca de comentario que aparezca.
     *
     * @param linea linea original
     * @return la parte util, que puede quedar vacia
     */
    private String quitarComentario(String linea) {
        int corte = linea.length();
        int puntoComa = linea.indexOf(COMENTARIO_PUNTO_COMA);
        int barras = linea.indexOf(COMENTARIO_BARRAS);
        if (puntoComa >= 0) {
            corte = Math.min(corte, puntoComa);
        }
        if (barras >= 0) {
            corte = Math.min(corte, barras);
        }
        return linea.substring(0, corte);
    }
}
