# Fases 2 y 3: catálogo consolidado y base LibGDX

Fecha: 21 de septiembre de 2026

## Fase 2 — resultado

- Se eliminó la pantalla inicial de edad, su ruta, filtro, botón y valor de
  configuración activo.
- El inicio muestra categorías de juegos, no áreas ni edades.
- Las 111 rutas anteriores se conservaron como modos internos para no perder
  funcionalidad ni progreso durante la migración.
- El catálogo visible se redujo a 29 fichas consolidadas.
- Una prueba exige que cada identificador anterior aparezca exactamente una
  vez en la matriz. Si un juego queda fuera o duplicado, la compilación falla.

La matriz ejecutable vive en
`app/src/main/kotlin/com/miambiente/app/model/CatalogoConsolidado.kt`.

### Categorías definitivas

1. Lógica y rompecabezas
2. Palabras e idiomas
3. Números
4. Mundo y naturaleza
5. Creatividad y música
6. Coordinación y reflejos
7. Juegos de mesa
8. Arcade

### Criterio de compatibilidad

Las fichas consolidadas abren una pantalla de modos. Cada modo sigue usando su
identificador histórico y su progreso actual hasta que su reemplazo LibGDX o
Godot incluya una migración equivalente. Esto evita una eliminación masiva de
pantallas antes de que el nuevo juego alcance paridad.

## Fase 3 — arquitectura integrada

- Se agregó `game-core`, un módulo JVM/Kotlin independiente de Android.
- Se integró LibGDX 1.14.2 y su backend Android con binarios ARM/x86.
- Los juegos LibGDX se ejecutan en una actividad Android dedicada y regresan
  al catálogo sin WebView ni un segundo APK.
- La separación `game-core` permite probar reglas sin emulador y reutilizar el
  motor en herramientas de escritorio futuras.

### Primer bloque migrado

`Memoria y observación` ya ofrece una versión enriquecida LibGDX:

- renderizado OpenGL con `FitViewport` adaptable;
- bucle de juego en tiempo real;
- tablero 4×3 barajado;
- apertura de dos cartas, bloqueo durante la comprobación y cierre animado por
  tiempo;
- parejas resueltas persistentes durante la partida;
- contador de movimientos y retorno al Android anfitrión al terminar.

Los cuatro modos Compose anteriores permanecen debajo del acceso enriquecido
como referencia de paridad. Se retirarán cuando el juego LibGDX absorba
Memorama, Qué falta, Memoria por turnos y Búsqueda visual como niveles/modos.

## Orden de continuación de fase 3

1. Completar los cuatro modos de Memoria y observación.
2. Migrar Percepción sensorial, Formas y encajes y Orden y secuencias.
3. Migrar Palabras, Números, Mundo y Vida práctica.
4. Migrar Taller creativo, Coordinación y Laberintos.
5. Eliminar pantallas Compose sustituidas y migrar su progreso.

Juegos de mesa y Arcade no se migran en esta fase; mantienen el orden acordado
en el plan maestro.

## Referencias técnicas

La integración sigue la documentación oficial de LibGDX para el backend
Android y usa la versión estable 1.14.2. La aplicación anfitriona continúa
siendo Android/Compose y LibGDX solo controla la superficie de cada juego.
