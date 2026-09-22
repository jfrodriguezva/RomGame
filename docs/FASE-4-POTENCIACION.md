# Fase 4 — Potenciación individual

Inicio: 22 de septiembre de 2026

Estado: **terminada**. Esta fase cubre las 14 familias normales; Juegos de
mesa y Arcade permanecen fuera hasta las fases 5 y 6.

## Matriz de avance

| Familia | Mecánicas propias | Tutorial | Pausa/reinicio | Niveles | Resultado |
|---|---:|---:|---:|---:|---:|
| Memoria y observación | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Percepción sensorial | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Formas y encajes | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Orden y secuencias | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Palabras y sonidos | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Construye palabras | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Números y cantidades | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Clasifica el mundo | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Naturaleza y planeta | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Personas y comunidad | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Vida práctica | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Taller creativo | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Coordinación y reflejos | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |
| Laberintos y recorridos | Sí | Sí | Sí | 20 × modo | Puntuación y estrellas |

## Primer bloque potenciado

Las tres familias lógicas comparten controles coherentes sin compartir sus
reglas: Percepción conserva selección, clasificación y diferencias; Formas
conserva encaje, geometría, binomio y rompecabezas; Orden conserva sus seis
secuencias arrastrables.

Se añadió:

- tutorial contextual al entrar en cada modalidad;
- pausa que bloquea toda interacción de la partida;
- reinicio explícito sin abandonar el juego;
- selector de nivel consistente;
- resultado con puntuación y valoración de cero a tres estrellas;
- reglas unitarias para estrellas e instrucciones específicas.

Validación: tutorial de Percepción abierto desde el catálogo en emulador, sin
excepción fatal ni ANR. La fase no se dará por terminada hasta completar y
recorrer las 14 familias.

## Bloque de lenguaje potenciado

Las doce modalidades cuentan con instrucciones propias para vocales,
abecedario, sonidos iniciales, rimas, sílabas, mayúsculas, trazos, construcción
de palabras, clasificación gramatical e inglés. Pausa también bloquea los
gestos de trazo y arrastre, evitando que una partida avance detrás del panel.

Validación: tutorial y pausa de Abecedario recorridos en emulador; 123 pruebas
unitarias totales sin fallos.

## Bloque curricular central potenciado

Las 34 modalidades de Números, Clasifica el mundo, Naturaleza y planeta,
Personas y comunidad y Vida práctica ahora presentan una instrucción acorde a
su mecánica: conteo táctil, clasificación por arrastre, orden o selección. El
panel superior permite cambiar de nivel, reiniciar y pausar sin perder el
estado visible; mientras tutorial o pausa están activos se bloquean también
los gestos de arrastre.

Validación: tutorial de `Números → Contar` y pausa recorridos en emulador. APK
generado correctamente, sin excepción fatal ni ANR; 124 pruebas unitarias
totales sin fallos.

## Bloque creativo, coordinación y recorridos potenciado

Las 14 modalidades finales tienen tutorial contextual y bloqueo completo de
entrada durante tutorial o pausa, incluidos dibujo y drag-and-drop. Se
mantienen los aciertos estrictos del Globo, la canasta por arrastre y los
laberintos con tamaño creciente. El resultado muestra puntuación y estrellas.

Validación: Pizarra recorrida desde el catálogo con tutorial, trazo y pausa;
sin excepción fatal ni ANR.

## Auditoría final de Memoria

Memoria quedó alineada con el resto mediante reinicio, pausa, tutorial por sus
cuatro modalidades y resultado con estrellas. Sus esperas de vista previa,
ocultamiento de cartas y turno de computadora ya no avanzan mientras la pausa
está activa. También se normalizó el tamaño de texto de sus cinco controles.

Validación: Parejas recorrida desde el catálogo con tutorial y pausa; controles
sin solapamiento y sin excepción fatal ni ANR. El cierre registra 125 pruebas
unitarias sin fallos para el proyecto.
