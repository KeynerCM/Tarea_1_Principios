package com.mycompany.minipc.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la lectura de archivos de codigo ensamblador.
 */
class CargadorASMTest {

    private final CargadorASM cargador = new CargadorASM();

    private File escribir(Path carpeta, String nombre, String contenido) throws IOException {
        Path archivo = carpeta.resolve(nombre);
        Files.writeString(archivo, contenido, StandardCharsets.UTF_8);
        return archivo.toFile();
    }

    @Test
    @DisplayName("Lee las lineas de un archivo .asm")
    void leeLasLineas(@TempDir Path carpeta) throws Exception {
        File archivo = escribir(carpeta, "file.asm",
                "MOV AX, 5\nMOV BX, 3\nLOAD AX\n");

        List<String> lineas = cargador.leer(archivo);

        assertEquals(3, lineas.size());
        assertEquals("MOV AX, 5", lineas.get(0));
        assertEquals("LOAD AX", lineas.get(2));
    }

    @Test
    @DisplayName("Conserva las lineas vacias, que el ensamblador ya descarta")
    void conservaLineasVacias(@TempDir Path carpeta) throws Exception {
        File archivo = escribir(carpeta, "file.asm", "MOV AX, 5\n\nLOAD AX\n");
        assertEquals(3, cargador.leer(archivo).size());
    }

    @Test
    @DisplayName("Rechaza archivos que no tengan extension .asm")
    void rechazaOtrasExtensiones(@TempDir Path carpeta) throws Exception {
        File archivo = escribir(carpeta, "programa.txt", "MOV AX, 5\n");
        assertThrows(IllegalArgumentException.class, () -> cargador.leer(archivo));
    }

    @Test
    @DisplayName("La extension se reconoce sin distinguir mayusculas")
    void extensionSinDistinguirMayusculas(@TempDir Path carpeta) throws Exception {
        File mayusculas = escribir(carpeta, "PROGRAMA.ASM", "MOV AX, 5\n");
        assertTrue(cargador.tieneExtensionValida(mayusculas));
        assertEquals(1, cargador.leer(mayusculas).size());

        File otro = escribir(carpeta, "programa.asmx", "MOV AX, 5\n");
        assertFalse(cargador.tieneExtensionValida(otro));
    }

    @Test
    @DisplayName("Un archivo inexistente da un error claro")
    void archivoInexistente(@TempDir Path carpeta) {
        File archivo = carpeta.resolve("no-esta.asm").toFile();
        IOException e = assertThrows(IOException.class, () -> cargador.leer(archivo));
        assertTrue(e.getMessage().contains("no existe"), e.getMessage());
    }

    @Test
    @DisplayName("Una carpeta no se acepta como archivo")
    void rechazaCarpetas(@TempDir Path carpeta) throws Exception {
        Path subcarpeta = Files.createDirectory(carpeta.resolve("programas.asm"));
        IOException e = assertThrows(IOException.class,
                () -> cargador.leer(subcarpeta.toFile()));
        assertTrue(e.getMessage().contains("no apunta a un archivo"), e.getMessage());
    }

    @Test
    @DisplayName("Un archivo guardado con codificacion de Windows tambien se lee")
    void leeCodificacionDeWindows(@TempDir Path carpeta) throws Exception {
        Path archivo = carpeta.resolve("acentos.asm");
        // Bytes invalidos en UTF-8, validos en ISO-8859-1: el cargador reintenta.
        Files.write(archivo, "; suma básica\nMOV AX, 5\n"
                .getBytes(StandardCharsets.ISO_8859_1));

        List<String> lineas = cargador.leer(archivo.toFile());
        assertEquals(2, lineas.size());
        assertEquals("MOV AX, 5", lineas.get(1));
    }

    @Test
    @DisplayName("No se acepta un archivo nulo")
    void rechazaNulo() {
        assertThrows(IllegalArgumentException.class, () -> cargador.leer(null));
        assertFalse(cargador.tieneExtensionValida(null));
    }
}
