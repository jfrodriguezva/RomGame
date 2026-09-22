# Fase 5 — Juegos de mesa

Inicio: 22 de septiembre de 2026

Estado: **en ejecución**. La fase 4 quedó cerrada antes de iniciar este trabajo.

## Alcance consolidado

| Familia | Modalidades | Estado de reglas heredadas | Migración |
|---|---|---|---|
| Alineación y duelo | Gato, Cuatro en línea, Piedra-papel-tijera | Gato con minimax; Conecta 4 con detección multidireccional; duelo contra CPU y local | Primer bloque |
| Dados y recorridos | Oca, Serpientes y escaleras, Dado de retos | Dado animado, avance y casillas especiales | Segundo bloque |
| Solitarios | Klondike, Araña | Reglas de secuencia, fundaciones, reparto y movimientos múltiples | Tercer bloque |
| Lotería y bingo | Lotería mexicana, Bingo con imágenes | Baraja/cartón, canto y marcado | Cuarto bloque |
| Estrategia de tablero | Damas, Damas chinas, Ajedrez | Captura obligatoria, saltos, jaque y CPU | Quinto bloque |
| Deducción y fichas | Dominó, Adivina quién | Dominó doble-6 y eliminación por atributos | Sexto bloque |

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
