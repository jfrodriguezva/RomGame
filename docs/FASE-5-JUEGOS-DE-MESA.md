# Fase 5 — Juegos de mesa

Inicio: 22 de septiembre de 2026

Estado: **terminada**. La fase 4 quedó cerrada antes de iniciar este trabajo.

## Alcance consolidado

| Familia | Modalidades | Estado de reglas heredadas | Migración |
|---|---|---|---|
| Alineación y duelo | Gato, Cuatro en línea, Piedra-papel-tijera | Reglas y superficie LibGDX con arrastre/toque, CPU y modo local | Terminado |
| Dados y recorridos | Oca, Serpientes y escaleras, Dado de retos | Reglas y superficie LibGDX con dado y ficha arrastrable | Terminado |
| Solitarios | Klondike, Araña | Reglas reales y mesa LibGDX con secuencias arrastrables | Terminado |
| Lotería y bingo | Lotería mexicana, Bingo con imágenes | Cartones LibGDX con marcador arrastrable y canto estricto | Terminado |
| Estrategia de tablero | Damas, Damas chinas, Ajedrez | Reglas puras y tablero LibGDX arrastrable con CPU/local | Terminado |
| Deducción y fichas | Dominó, Adivina quién | Dominó doble-6 y eliminación por atributos | Terminado |

Total: 6 familias y 15 modalidades.

## Criterio de terminación

Cada modalidad debe:

- conservar o ampliar sus reglas actuales, sin sustituirlas por una mecánica
  educativa genérica;
- permitir tomar y soltar fichas, cartas o marcadores mediante drag-and-drop;
- ofrecer selección por toque como alternativa accesible;
- señalar origen seleccionado, destinos válidos y jugada inválida;
- bloquear entrada durante el turno de CPU o una animación;
- soportar reinicio, pausa, tutorial y resultado;
- guardar partidas terminadas y progreso;
- tener pruebas unitarias de reglas, compilación, lint y recorrido en emulador.

## Arquitectura objetivo

Las reglas se trasladan de las pantallas Compose a `game-core` como estado puro
y comprobable. Un tablero LibGDX común gestiona coordenadas, selección,
arrastre, destinos y animación; cada modalidad conserva su generador de
movimientos, condición de victoria y rival. Las rutas Compose sólo se retiran
cuando la modalidad correspondiente alcanza paridad funcional.

## Orden de trabajo

1. Alineación y duelo, para validar turnos, CPU y fichas arrastrables.
2. Dados y recorridos, para validar animación y movimiento sobre rutas.
3. Solitarios, por ser el bloque de drag-and-drop más exigente.
4. Lotería y bingo, con marcadores arrastrables y alternativa por toque.
5. Estrategia, reutilizando la superficie probada con reglas más complejas.
6. Deducción y fichas, cerrando con cadena de dominó y descarte visual.

No se iniciará la fase 6 de Arcade antes de completar esta matriz.

## Primer avance ejecutable

El bloque Alineación y duelo ya abre sus tres modalidades en una actividad
LibGDX desde el catálogo. La superficie acepta tanto arrastrar desde el origen
como tocar origen y destino. Se incorporó:

- Gato 3×3 con CPU minimax;
- Cuatro en línea convencional 7×6, gravedad, victoria horizontal, vertical y
  diagonal, y CPU que gana o bloquea antes de ocupar el centro;
- Piedra-papel-tijera al mejor de tres rondas contra CPU;
- persistencia de partidas terminadas;
- ocho pruebas nuevas de reglas.

Validación inicial: Gato abierto desde Juegos de mesa, ficha X arrastrada al
tablero y respuesta O de minimax renderizada correctamente; sin excepción
fatal ni ANR. El bloque ya incluye modo contra CPU y dos jugadores locales,
tutorial, pausa y reinicio. En Piedra-papel-tijera local, la primera elección
se oculta antes de entregar el dispositivo al segundo jugador. Falta completar
el recorrido de las otras dos modalidades antes de retirar Compose.

## Dados y recorridos

Oca y Serpientes conservan el recorrido serpenteante, llegada con tirada
exacta y sus saltos, puentes, pozos, escaleras y serpientes. Después de tirar,
la única casilla de destino se resalta y la ficha debe arrastrarse hasta ella;
también puede seleccionarse y colocarse por toque. Dado de retos conserva 40
actividades y exige arrastrar el dado a su zona de lanzamiento.

Se añadieron pausa, reinicio, tutorial y seis pruebas de reglas. Validación:
Oca abierta desde el catálogo, tirada de dado, destino resaltado y ficha
arrastrada a la casilla correcta; sin excepción fatal ni ANR. El proyecto suma
139 pruebas unitarias sin fallos.

## Solitarios

Klondike conserva 52 cartas, siete columnas, mazo y descarte de una carta,
fundaciones por palo, alternancia de color y movimiento de secuencias. Araña
conserva 104 cartas de un palo, diez columnas, reparto de diez, secuencias
descendentes y retirada automática de cada bloque Rey→As.

Ambos permiten arrastrar cartas o secuencias completas y usar selección por
toque. Incluyen tutorial, pausa, reinicio, validación de destinos y revelado
automático de la nueva carta superior. Se añadieron nueve pruebas de reglas.

Validación inicial: Klondike abierto desde el catálogo con reparto 1–7, 24
cartas en mazo y robo al descarte; sin excepción fatal ni ANR. El total del
proyecto asciende a 148 pruebas unitarias sin fallos.

## Lotería y bingo

Lotería usa un mazo infantil de 36 cartas distintas y cartón tradicional 4×4.
El gritón puede cantar cartas ausentes y la acción “No está” sólo avanza cuando
la afirmación es correcta. Bingo conserva el cartón 3×3 y gana con fila,
columna o diagonal.

En ambos, el marcador se arrastra desde la reserva hasta la casilla cantada o
se coloca por selección y toque. Una casilla distinta o ya marcada se rechaza.
Se añadieron tutorial, pausa, reinicio y cinco pruebas de mazo, marcado y
patrones ganadores.

Validación inicial: Lotería abierta desde el catálogo con canto, marcador y
cartón 4×4; se corrigió el tamaño de texto de nombres largos. Compilación y 153
pruebas unitarias sin fallos.

## Estrategia de tablero

Damas conserva captura obligatoria en todo el tablero, coronación y capturas
encadenadas. Damas chinas permite pasos y cadenas de saltos sobre cualquier
canica sin capturarla. Ajedrez conserva movimientos por pieza, doble paso de
peón, promoción, bloqueo de piezas, jaque y rechazo de movimientos que dejan
al propio rey atacado.

Los tres comparten tablero LibGDX con piezas arrastrables, selección por toque,
destinos legales resaltados, CPU y dos jugadores locales, tutorial, pausa y
reinicio. Se corrigió la orientación para que el bando del jugador aparezca
abajo. Se añadieron ocho pruebas específicas.

Validación inicial: Damas abierto desde el catálogo, tablero y tutorial
renderizados sin excepción fatal ni ANR.

## Deducción y fichas

Dominó conserva el set doble-6 de 28 fichas, reparto de siete, pozo, cadena con
extremos izquierdo y derecho elegibles, orientación de fichas, pase, partida
bloqueada y desempate por puntos. Incluye CPU y dos jugadores locales.

Adivina quién usa 12 candidatos y preguntas con respuesta sí/no por color,
tamaño y hábitat. Los candidatos se descartan de forma coherente con la
respuesta; además, cada carta se puede arrastrar manualmente a las zonas de
descarte o adivinanza.

Ambos fueron abiertos desde el catálogo en emulador, mostraron tutorial y
tablero sin excepción fatal ni ANR. Se retiraron las 15 pantallas Compose de
mesa y sus pruebas duplicadas. Validación final desde limpio: 103 pruebas
vigentes, Android Lint sin errores y APK debug generado.
