# Mini PC — Simulador del ciclo de instrucción
## Integrantes:
### 2024108270 Keyner Cerdas Morales

### Estado del proyecto: 1
### Enlace del video: [pendiente de agregar]
Recordar que el video debe ser público y con sonido para ser visto por el profesor

---

## Descripción

Simulador visual del ciclo de instrucción de un procesador, basado en la máquina
hipotética que describe Stallings en *Operating Systems: Internals and Design
Principles*, 9na edición, capítulo 1.3.

El programa lee un archivo de texto con instrucciones similares al lenguaje
ensamblador, las traduce a binario, las carga en una memoria simulada y las
ejecuta una por una, mostrando en pantalla cómo cambian el contador de programa,
el registro de instrucción, el acumulador, los registros de propósito general y
el Bloque de Control de Proceso.

La ejecución puede seguirse **paso a paso** o dejarse correr de forma
**automática** a una velocidad configurable.

> Todo es simulado. La "memoria" es un arreglo de objetos Java y los "registros"
> son campos enteros. El programa no accede al hardware de la computadora: lo
> único que hace fuera de su propio proceso es leer el archivo `.asm` del disco.

## Requisitos

- **JDK 17** o superior
- **Maven 3.8+** (NetBeans lo trae incorporado)
- Sistema operativo con entorno gráfico (Windows, Linux o macOS)

## Compilación y ejecución

### Desde NetBeans

1. `File` → `Open Project…` y seleccionar la carpeta `Programa/minipc`.
2. Clic derecho sobre el proyecto → `Run`.

### Desde la terminal

```bash
cd Programa/minipc
mvn clean package
java -jar target/minipc-1.0.jar
```

### Para correr las pruebas

```bash
cd Programa/minipc
mvn test
```

## Guía de uso

| Botón | Qué hace |
|---|---|
| **Cargar .asm** | Abre el selector de archivos, valida el formato y carga el programa en memoria |
| **Ejecutar** | Corre el programa completo de forma automática |
| **Paso a paso** | Ejecuta una sola instrucción |
| **Reiniciar** | Vuelve al inicio del programa sin descargarlo de memoria |
| **Limpiar** | Vacía la memoria de usuario, los registros, las tablas y la consola |
| **Configurar** | Tamaño de memoria, límite de kernel y velocidad de ejecución |
| **Estadísticas** | Resumen de la ejecución |

La ventana se divide en cuatro áreas:

- **Instrucciones** — el programa fuente junto a su traducción binaria. La fila
  ámbar es la próxima a ejecutarse; las grises ya se ejecutaron.
- **Memoria** — todas las posiciones. Gris la zona de kernel, azul las celdas
  con instrucciones, ámbar la posición que apunta el PC.
- **BCP actual** — los atributos del Bloque de Control de Proceso, agrupados en
  cinco secciones.
- **Consola** — registro de la actividad, con la hora de cada evento.

## Juego de instrucciones

| Mnemónico | Opcode | Sintaxis | Semántica |
|---|---|---|---|
| `LOAD` | `0001` | `LOAD Rx` | `AC ← Rx` |
| `STORE` | `0010` | `STORE Rx` | `Rx ← AC` |
| `MOV` | `0011` | `MOV Rx, n` | `Rx ← n` |
| `SUB` | `0100` | `SUB Rx` | `AC ← AC − Rx` |
| `ADD` | `0101` | `ADD Rx` | `AC ← AC + Rx` |

**Registros:** `AX = 0001`, `BX = 0010`, `CX = 0011`, `DX = 0100`

## Formato binario

### Instrucción — palabra de 16 bits

```
 bit 15   12 11    8 7                     0
   +--------+--------+----------------------+
   | OPCODE | REGIS. |       OPERANDO       |
   +--------+--------+----------------------+
     4 bits   4 bits         8 bits
```

Ejemplo: `MOV AX, 5` → `0011 0001 00000101`

### Entero — 8 bits en signo-magnitud

```
 bit 7                            bit 0
   +---+-------------------------------+
   | S |           MAGNITUD            |
   +---+-------------------------------+

   S = 0 positivo  |  S = 1 negativo
```

Ejemplo: `-8` → `10001000`

- Rango representable: **−127 a 127**
- El formato admite dos ceros (`00000000` y `10000000`); el programa normaliza
  siempre al positivo.

## Formato del archivo `.asm`

Una instrucción por línea. Se aceptan:

- Comentarios con `;` o `//`, al inicio de la línea o después de una instrucción
- Líneas vacías, que no ocupan posición en memoria
- La coma como separador opcional: `MOV AX, 5` y `MOV AX 5` son equivalentes
- Mayúsculas y minúsculas indistintas

```asm
; Programa de ejemplo del enunciado
MOV AX, 5
MOV BX, 3
LOAD AX
ADD BX
SUB AX
STORE AX
MOV BX, -8
```

## Programas de ejemplo

En la carpeta [`Ejemplo/`](Ejemplo/):

| Archivo | Contenido |
|---|---|
| `file.asm` | El programa de la lámina 6 del enunciado, 7 instrucciones |
| `programa-largo.asm` | 35 instrucciones, usa las cinco operaciones y los cuatro registros |
| `ejemplo2.asm` | Programa corto con valores negativos |
| `error-sintaxis.asm` | Provoca tres errores de sintaxis a la vez |
| `desbordamiento.asm` | Provoca un desbordamiento aritmético en ejecución |

## Modelo de memoria

```
  0                    63 64                            255
  +----------------------+--------------------------------+
  |     ZONA KERNEL      |        ZONA DE USUARIO         |
  |  (sistema operativo) |   (aquí se carga el programa)  |
  +----------------------+--------------------------------+
```

| Parámetro | Por defecto | Restricción |
|---|---|---|
| Tamaño de memoria | 256 | mínimo 128 |
| Límite de kernel | 64 | mínimo 16, menor que el tamaño total |
| Zona de usuario | 64 – 255 | el resto de la memoria |

Cada posición guarda una palabra de 16 bits, de modo que **una línea de programa
ocupa exactamente una posición**.

El proceso de usuario no puede leer la zona de kernel: el intento lanza una
excepción. Es el equivalente didáctico de una violación de segmento.

## Bloque de Control de Proceso

Reúne todo lo que el sistema operativo necesitaría para suspender el proceso y
reanudarlo exactamente donde quedó. Se actualiza después de cada instrucción.

| Sección | Atributos |
|---|---|
| Proceso | PID, nombre del programa, estado |
| Contexto del CPU | PC, IR (binario), IR (texto), AC |
| Registros | AX, BX, CX, DX |
| Memoria del proceso | dirección base, límite |
| Contabilidad | instrucciones ejecutadas, ciclos de reloj, hora de creación |

**Estados del proceso:** `NUEVO → LISTO → EJECUCION → TERMINADO`, con una
transición alternativa a `BLOQUEADO_ERROR` ante un desbordamiento aritmético.

## Validaciones

El programa rechaza y explica:

- Operación desconocida, con el número de línea
- Registro inexistente, con el número de línea
- Registro u operando faltante
- Operandos sobrantes
- Valor no numérico
- Valor fuera del rango −127 a 127
- Archivo sin ninguna instrucción
- Archivo con extensión distinta de `.asm`
- Programa que no cabe en la zona de usuario, indicando cuántas posiciones
  requiere y cuántas hay disponibles
- Desbordamiento aritmético durante la ejecución

Los errores de sintaxis **se reportan todos juntos**: el ensamblador recorre el
archivo completo antes de fallar, para que el usuario corrija en una sola pasada.

## Decisiones de diseño

**Opcodes de 4 bits, no de 3.** La lámina 5 del enunciado lista los opcodes con
tres bits y la lámina 7 con cuatro. Son el mismo valor con un cero a la
izquierda; se usó la versión de cuatro bits, que es la de los ejemplos binarios
del propio enunciado y la de la figura 1.3d de Stallings.

**Palabra de memoria de 16 bits, una línea por posición.** Permite que una
instrucción completa quepa en una sola celda, como pide el enunciado.

**Enteros en signo-magnitud escritos a mano.** Java usa complemento a dos
internamente, de modo que `Integer.toBinaryString(-8)` produciría `11111000` en
lugar de `10001000`. La conversión está implementada explícitamente en
`util/BinUtil`.

**Aritmética resuelta en decimal.** Las sumas y restas usan enteros normales de
Java; el binario existe solo en tres momentos: al ensamblar, al decodificar la
palabra leída de memoria, y al mostrar los valores en pantalla.

**El desbordamiento detiene el proceso** en lugar de saturar el valor en el
límite. Es más honesto con el formato de 8 bits y permite demostrar el manejo
del error.

**La carga en memoria es atómica.** Se valida el espacio antes de limpiar, de
modo que un programa que no cabe no destruye el que ya estaba cargado.

**La ejecución automática usa `javax.swing.Timer`**, nunca un bucle. Un bucle
dentro del hilo de despacho de eventos congelaría la ventana hasta terminar y no
se vería nada de la ejecución.

**El núcleo no conoce Swing.** Los paquetes `core` e `isa` no importan ninguna
clase de la interfaz gráfica. La comunicación va por el patrón Observer, y la
ventana implementa la interfaz `gui/VistaPrincipal`, contra la cual programa el
controlador.

**Diferencia con Stallings:** en el libro `LOAD`, `STORE` y `ADD` operan sobre
direcciones de memoria; en este enunciado operan sobre registros. La consecuencia
es que ninguna instrucción escribe datos en memoria, y la zona de usuario solo
guarda código.

## Estructura del proyecto

```
Tarea_1_Principios/
├── Ejemplo/                      programas .asm de prueba
├── Programa/minipc/              proyecto Maven
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/mycompany/minipc/
│       │   ├── MiniPC.java       punto de entrada
│       │   ├── util/             conversiones binarias
│       │   ├── isa/              juego de instrucciones y ensamblador
│       │   ├── core/             procesador, memoria, registros, BCP
│       │   ├── io/               lectura de archivos
│       │   ├── excepciones/      errores del dominio
│       │   └── gui/              interfaz, controlador y modelos de tabla
│       └── test/java/…           92 pruebas automáticas
└── README.md
```

## Pruebas automáticas

El proyecto incluye **92 pruebas** en 8 clases, que verifican el simulador sin
abrir ninguna ventana.

| Clase | Pruebas | Verifica |
|---|---|---|
| `BinUtilTest` | 10 | Conversión signo-magnitud en todo el rango |
| `InstruccionTest` | 10 | La codificación binaria de la lámina 7 |
| `EnsambladorTest` | 11 | Sintaxis, comentarios y reporte de errores |
| `BancoRegistrosTest` | 11 | Registros, celdas de memoria y estados |
| `MemoriaTest` | 12 | Zonas, carga atómica y validación de espacio |
| `ProcesadorTest` | 15 | El ciclo fetch-execute contra la lámina 6 |
| `CargadorASMTest` | 8 | Lectura y validación de archivos |
| `ControladorPrincipalTest` | 15 | La cadena completa con una vista de prueba |

La prueba central, `ProcesadorTest.reproduceLaTablaDelEnunciado`, ejecuta el
programa de ejemplo instrucción por instrucción y compara AC, AX y BX contra los
siete estados de la lámina 6 del enunciado.

## Referencia

Stallings, W. *Operating Systems: Internals and Design Principles*, 9na edición.
Capítulo 1, sección 1.3 «Instruction Execution» — figuras 1.2, 1.3 y 1.4.
