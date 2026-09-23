# RominaGame — Manual de usuario

## Inicio y categorías

La pantalla inicial muestra 29 juegos agrupados en ocho categorías:

1. Lógica y rompecabezas
2. Palabras e idiomas
3. Números
4. Mundo y naturaleza
5. Creatividad y música
6. Coordinación y reflejos
7. Juegos de mesa
8. Arcade

No hay selector, bloqueo ni recomendación por edad. El usuario elige una
categoría, abre una familia y selecciona la modalidad o comienza la campaña.

## Juegos LibGDX

Los juegos normales ofrecen selector de modalidad y nivel, tutorial, pausa,
reinicio, puntuación, estrellas y pantalla de resultado. Los juegos de mesa
permiten arrastrar fichas, cartas o piezas y también tocar origen y destino.

Las familias de mesa incluyen alineación, recorridos con dados, solitarios,
lotería y bingo, estrategia de tablero, dominó y deducción.

## Arcade

Arcade contiene Bloques, La víbora, Rompe ladrillos, Mosaico sorpresa,
Vaqueros del ocaso, Comepuntos, Rescate de nieve, Escuadrón estelar, Gran
premio y Carreras. Se ejecutan a pantalla horizontal con controles táctiles,
pausa, reinicio, vidas o puntuación y progresión propia.

## Ajustes y progreso

El engrane de la pantalla inicial permite cambiar nombre, sonido, voz,
vibración, música y modo calmado. Partidas, niveles y estrellas se guardan
localmente. Al actualizar desde una versión anterior, el progreso conocido se
conserva y se agrega a la familia consolidada correspondiente.

## Privacidad

La aplicación funciona sin cuenta y sin conexión. No solicita permiso de
Internet ni envía progreso, nombre o actividad a servidores. Borrar los datos
de la aplicación desde Android elimina la configuración y el progreso local.

## Instalación de prueba

Con depuración USB habilitada:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Para una publicación se debe instalar el APK derivado del AAB firmado, no el
APK de depuración.
