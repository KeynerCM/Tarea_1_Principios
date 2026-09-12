package com.mycompany.minipc.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Lee archivos de codigo ensamblador del disco.
 *
 * No interpreta el contenido: solo entrega las lineas de texto. Traducir
 * es tarea del Ensamblador.
 */
public class CargadorASM {

    /** Unica extension aceptada, como pide el enunciado. */
    public static final String EXTENSION = "asm";

    /**
     * Lee un archivo .asm completo.
     *
     * Intenta primero UTF-8. Si el archivo fue guardado con la codificacion
     * de Windows, reintenta con ISO-8859-1 en lugar de fallar: el Bloc de
     * notas de una maquina en espanol produce archivos asi.
     *
     * @param archivo archivo a leer
     * @return las lineas del archivo, sin el salto de linea final
     * @throws IOException              si el archivo no existe o no se puede leer
     * @throws IllegalArgumentException si la extension no es .asm
     */
    public List<String> leer(File archivo) throws IOException {
        validar(archivo);
        try {
            return Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            return Files.readAllLines(archivo.toPath(), StandardCharsets.ISO_8859_1);
        }
    }

    /**
     * Comprueba que el archivo se pueda leer y tenga la extension correcta.
     *
     * @param archivo archivo a evaluar
     * @throws IOException              si no existe o no es un archivo legible
     * @throws IllegalArgumentException si la extension no es .asm
     */
    private void validar(File archivo) throws IOException {
        if (archivo == null) {
            throw new IllegalArgumentException("No se indico ningun archivo");
        }
        if (!archivo.exists()) {
            throw new IOException("El archivo no existe: " + archivo.getAbsolutePath());
        }
        if (!archivo.isFile()) {
            throw new IOException("La ruta no apunta a un archivo: " + archivo.getAbsolutePath());
        }
        if (!archivo.canRead()) {
            throw new IOException("No hay permiso para leer el archivo: " + archivo.getName());
        }
        if (!tieneExtensionValida(archivo)) {
            throw new IllegalArgumentException("El archivo debe tener extension ." + EXTENSION
                    + ", se recibio \"" + archivo.getName() + "\"");
        }
    }

    /**
     * @param archivo archivo a evaluar
     * @return true si el nombre termina en .asm, sin distinguir mayusculas
     */
    public boolean tieneExtensionValida(File archivo) {
        return archivo != null
                && archivo.getName().toLowerCase().endsWith("." + EXTENSION);
    }
}
