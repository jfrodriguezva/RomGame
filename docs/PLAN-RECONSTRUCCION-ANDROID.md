# Plan maestro de reconstrucción de RominaGame

Estado: **propuesta para aprobación antes de ejecutar cambios destructivos**  
Fecha: 21 de septiembre de 2026

> **Aprobado y en ejecución.** Las fases 0 y 1 comenzaron el 21 de septiembre
> de 2026. El proyecto Android nativo fue promovido a la raíz y las capas
> React/Next/Capacitor fueron retiradas.

## 1. Objetivo acordado

Reconstruir RominaGame como una plataforma de juegos tradicionales para
Android, sin React, Next.js, Capacitor ni WebView. La nueva aplicación tendrá
menos fichas en el catálogo, pero cada ficha contendrá un juego completo con
modos, niveles, progresión y mejor presentación.

La aplicación dejará de organizar o bloquear contenido por edad. El catálogo se
organizará exclusivamente por categorías, incluyendo dos categorías propias:
**Juegos de mesa** y **Arcade**.

## 2. Punto de partida confirmado

El repositorio contiene actualmente tres capas:

1. Una aplicación web Next.js/React en la raíz (`app`, `components`, `data`,
   `lib`, `public`, configuración Node y artefactos `.next`/`node_modules`).
2. Un empaquetado Android antiguo de Capacitor en `android`.
3. Una aplicación Android nativa en `android-nativo`, con Kotlin, Jetpack
   Compose, DataStore, pruebas unitarias y el catálogo actual de 111 entradas.

La tercera capa será la base del producto. Las dos primeras se retirarán
después de elaborar un inventario de recursos reutilizables y confirmar que
ningún archivo exclusivo vaya a perderse.

Hay cambios locales sin publicar en `android-nativo`; se conservarán y se
creará un punto de recuperación antes de la limpieza.

## 3. Arquitectura objetivo

### Aplicación anfitriona: Android nativo

- Kotlin y Jetpack Compose para inicio, categorías, perfil local, ajustes,
  progreso, selector de niveles, accesibilidad y navegación.
- DataStore o base local para progreso, estrellas, configuración y partidas.
- Sin servidor obligatorio, navegador interno ni conexión permanente.
- Un solo APK/AAB, una identidad de aplicación y un sistema de progreso.

### Juegos normales y juegos de mesa: LibGDX

- Bucle estable de renderizado y actualización.
- Scene2D para controles, tableros y arrastre real.
- Sprites, animaciones, audio, partículas, cámara, interpolación y gestos.
- Lógica separada del render para poder probar reglas sin emulador.
- Drag-and-drop como interacción principal de cartas, fichas y piezas.

### Arcade: Godot integrado en Android

- Cada arcade será una escena/proyecto modular de Godot.
- La aplicación Android abrirá el juego seleccionado y recibirá resultados,
  progreso, pausa y salida mediante una capa de integración definida.
- Godot aportará físicas, AnimationTree, TileMap, cámara, partículas,
  audio, gamepad/táctil y renderizado 2D/3D.
- Los arcades se implementarán al final para no fijar la integración sobre un
  catálogo que todavía esté cambiando.

Esta arquitectura sigue siendo Android nativo: no incluye React ni WebView.
LibGDX y Godot son motores ejecutados dentro del producto Android.

## 4. Fases de ejecución

Cada fase termina con compilación, pruebas, recorrido en emulador y un punto de
control independiente. No se inicia la fase siguiente con fallos conocidos de
severidad alta.

### Fase 0 — Respaldo, inventario y línea base

1. Registrar el estado Git y separar cambios locales existentes.
2. Generar inventarios del catálogo, rutas, pantallas, pruebas y recursos.
3. Identificar imágenes, audio, fuentes o datos que solo existan en la web.
4. Guardar los recursos reutilizables dentro de `android-nativo`.
5. Construir y probar la versión nativa antes de borrar nada.

**Salida:** inventario auditable, APK de referencia y punto de recuperación.

### Fase 1 — Retirar React, Next.js y Capacitor

Una vez validado el inventario se retirarán:

- `app`, `components`, `data`, `lib`, `phaser`, `.next` y `node_modules`.
- `package.json`, `package-lock.json`, configuraciones Next/React/Tailwind y
  scripts exclusivos del producto web.
- El proyecto `android` generado por Capacitor y `capacitor.config.ts`.
- Documentación obsoleta que describa instalación o despliegue web.

`public` no se borrará en bloque hasta clasificar sus recursos. Lo reutilizable
se migrará a `android-nativo/app/src/main/assets` o `res`; lo restante se
eliminará con una lista explícita.

La raíz del repositorio pasará a representar al proyecto Android; se decidirá
en esta fase si `android-nativo` se conserva como carpeta o se promueve a la
raíz para simplificar CI y documentación.

**Salida:** repositorio Android compilable sin Node, npm, Next ni Capacitor.

### Fase 2 — Consolidar el catálogo

Se eliminará `edadMinima` del modelo y toda pantalla, texto, filtro o ajuste de
edad. Ningún juego quedará bloqueado por edad.

La consolidación no consistirá en esconder accesos: cada familia tendrá una
sola ficha y dentro ofrecerá mundos, modos o niveles diferenciados.

Propuesta inicial de familias:

| Juego consolidado | Contenido actual que absorbe |
|---|---|
| Percepción sensorial | colores, textura, temperatura, peso, sabor, olfato, sombras, diferencias |
| Formas y encajes | formas, sólidos, lados, orificios, cilindros, binomio |
| Orden y secuencias | torre rosa, escalera marrón, días, estaciones, ciclos, rutinas |
| Clasifica el mundo | vivo/no vivo, hábitat, dieta, fruta/verdura, agua, día/noche, transportes |
| Palabras y sonidos | vocales, abecedario, sonidos iniciales, rimas, sílabas, mayúsculas, letras de lija |
| Construye palabras | alfabeto móvil, singular/plural, el/la e inglés inicial |
| Números y cantidades | contar, husos, barras, números, tabla del cien, banco dorado, pares/impares |
| Vida práctica | pinza, transferencias, mesa, manos, rutina y ejercicios de vida práctica |
| Naturaleza y planeta | animales, planta, continentes, tierra/agua, sistema solar, clima y banderas |
| Taller creativo | pizarra, colorear, collage, trazos y xilófono como modos independientes |
| Memoria y observación | memorama, qué falta, memoria por turnos, objetos y diferencias avanzadas |
| Coordinación y reflejos | burbujas, globo, estrellas, topo, reflejo y vibración |
| Laberintos y recorridos | laberinto y araña como mundos/reglas diferentes |
| Solitarios | solitario clásico y araña como modos, con dificultades |
| Juegos de dados y recorrido | oca, serpientes y escaleras y dado de retos como tableros/modos |
| Juegos de alineación | gato y conecta cuatro, manteniendo tableros y reglas propios |

No se unirán juegos solo porque compartan categoría. Ajedrez, damas, dominó,
lotería o Adivina quién conservarán identidad cuando sus reglas no admitan
una progresión coherente dentro de otra familia.

Antes de modificar el catálogo se producirá una matriz definitiva
`juego actual -> juego destino -> modo/nivel -> datos que se conservan`. Esta
matriz será revisable y permitirá migrar el progreso existente.

**Meta inicial:** reducir 111 fichas a aproximadamente 25–35 juegos robustos.
El número final lo decidirá la matriz, no una cuota artificial.

### Fase 3 — Migración por bloques a LibGDX

Orden sugerido, de menor a mayor riesgo:

1. Clasificación, memoria, secuencias y encajes.
2. Lenguaje, matemáticas, cultura y vida práctica.
3. Taller creativo, coordinación y recorridos.
4. Juegos con simulación, movimiento continuo o cámara.

Cada bloque sustituirá sus pantallas Compose solo cuando alcance paridad
funcional. La pantalla anterior permanecerá disponible internamente durante la
comparación y se retirará al aprobar el bloque.

**Salida por juego:** tutorial, selector de nivel, pausa, reinicio, audio,
feedback visual, accesibilidad básica, persistencia y pruebas de reglas.

### Fase 4 — Potenciar cada juego consolidado

Se revisará uno por uno y no mediante una plantilla genérica. Cada juego debe
tener:

- curva de dificultad diseñada, no solo más velocidad;
- niveles hechos o generados con objetivos distintos;
- animación, efectos, audio y estados de victoria/derrota;
- instrucciones interactivas;
- controles táctiles consistentes y respuesta inmediata;
- guardado de nivel, puntuación, estrellas y mejor marca;
- pruebas unitarias de reglas y pruebas de inicio/reinicio/salida.

Los juegos de mesa y Arcade quedan fuera de esta fase y se realizan al final,
tal como fue solicitado.

### Fase 5 — Juegos de mesa

Los juegos de mesa pasarán a LibGDX/Scene2D con:

- fichas, cartas y piezas arrastrables;
- destino resaltado y previsualización de movimiento;
- retorno animado cuando un movimiento sea inválido;
- reglas completas, turnos, deshacer cuando proceda e historial;
- oponente local con niveles de dificultad;
- modo para dos personas en el mismo dispositivo cuando sea apropiado;
- animaciones de reparto, captura, coronación y final de partida.

El drag-and-drop será la interacción principal, pero se conservará una
alternativa accesible de tocar origen y destino.

Orden propuesto: solitarios, dominó, damas, damas chinas, gato/conecta cuatro,
oca/serpientes, lotería/bingo, Adivina quién y ajedrez al final por la
complejidad de reglas e inteligencia artificial.

### Fase 6 — Arcade en Godot

La categoría **Arcade** contendrá los clásicos internos existentes y seis
producciones principales:

1. Mosaico sorpresa: captura de territorio y enemigos sobre el campo.
2. Vaqueros del ocaso: acción lateral, plataformas, disparos y jefes.
3. Comepuntos: laberintos, puntos, energizantes y perseguidores con IA.
4. Rescate de nieve: plataformas, nieve acumulativa y bolas con cadenas.
5. Escuadrón estelar: shooter sobre rieles 3D, puntería, obstáculos y jefes.
6. Gran premio: conducción, rivales, circuitos, vueltas y progresión.

Tetris, La víbora, Rompe ladrillos, Atrapa al topo y otros arcades existentes
se evaluarán para migrarlos también a Godot y compartir menús, entrada, audio
y servicios de progreso.

#### Fidelidad permitida y límite de propiedad intelectual

La meta es reproducir con alta fidelidad la **modalidad**, reglas, sensación de
control, ritmo, tipos de enemigos, estructura de niveles y sistemas del arcade
de referencia. No se copiarán nombres comerciales, personajes, sprites,
música, voces, efectos, textos, mapas, circuitos ni niveles exactos protegidos.

Por tanto, "calca" se interpretará como paridad mecánica y de experiencia, no
como extracción o reproducción de contenido de las ROM originales. Se crearán
arte, audio, narrativa, niveles y marcas propios.

Cada arcade pasará por un prototipo jugable, vertical slice, producción de
niveles, balance, optimización y pruebas en dispositivo. No se construirán los
seis simultáneamente.

## 5. Categorías objetivo

La propuesta de navegación, sin edades, es:

- Lógica y rompecabezas
- Palabras e idiomas
- Números
- Mundo y naturaleza
- Creatividad y música
- Coordinación y reflejos
- Juegos de mesa
- Arcade

Las etiquetas educativas podrán mantenerse como información secundaria, pero
no determinarán acceso, edad ni visibilidad.

## 6. Modelo de datos y progreso

El nuevo modelo separará:

- `Game`: ficha única del catálogo.
- `Mode`: variante con reglas propias.
- `World` o `Campaign`: grupo temático.
- `Level`: reto concreto y sus parámetros.
- `Progress`: desbloqueo, resultado, estrellas y mejor marca.

Se escribirá una migración de los identificadores antiguos a los nuevos para
no descartar progreso sin avisar. Los registros sin equivalencia se conservarán
durante al menos una versión de migración.

## 7. Calidad y definición de terminado

Un bloque solo se considera terminado si:

1. Compila en debug y release.
2. Las reglas tienen pruebas unitarias deterministas.
3. Abre, juega, pausa, reinicia y sale sin perder el estado de la app.
4. No produce ANR, cierres ni crecimiento continuo de memoria.
5. Mantiene el objetivo de rendimiento en dispositivos Android de gama media.
6. Funciona con toque; los juegos de mesa además funcionan con arrastre.
7. Persiste y recupera progreso.
8. Ha sido recorrido en emulador y al menos un dispositivo físico antes de
   publicarse como estable.

Se mantendrá CI con pruebas, lint y construcción del APK/AAB. Los motores
tendrán pruebas de integración para entrada/salida y comunicación de progreso.

## 8. Decisiones que quedan fijadas al aprobar este documento

- `android-nativo` es la fuente de verdad inicial.
- React, Next, Phaser web y Capacitor se eliminan tras inventariar recursos.
- No existe selector, bloqueo ni filtrado por edades.
- Se reduce el número de fichas consolidando mecánicas relacionadas.
- Android/Compose gestiona la aplicación; LibGDX ejecuta juegos normales y de
  mesa; Godot ejecuta Arcade.
- Juegos de mesa y Arcade son los dos últimos grandes bloques.
- Juegos de mesa priorizan drag-and-drop.
- Arcade busca fidelidad mecánica con identidad visual, sonora y niveles
  originales.

## 9. Primer trabajo después de la aprobación

La primera entrega ejecutable será exclusivamente Fase 0 y Fase 1:

1. crear inventarios y respaldo;
2. rescatar recursos únicos;
3. retirar la aplicación web y Capacitor de manera verificable;
4. actualizar estructura, CI y documentación;
5. demostrar que la aplicación Android nativa compila y conserva sus pruebas.

No se consolidará ni migrará ningún juego en esa primera entrega. Esto evita
mezclar una limpieza destructiva con cambios funcionales difíciles de auditar.
