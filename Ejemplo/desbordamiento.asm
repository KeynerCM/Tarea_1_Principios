; ==========================================================
; desbordamiento.asm
; Demuestra la deteccion de desbordamiento aritmetico.
; Las tres primeras instrucciones se ejecutan sin problema;
; la cuarta intenta calcular 100 + 100 = 200, que no cabe en
; el formato de entero de 8 bits, y deja el proceso en el
; estado BLOQUEADO_ERROR.
; ==========================================================

MOV AX, 100
MOV BX, 100
LOAD AX
ADD BX
