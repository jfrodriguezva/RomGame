# Mi Ambiente — Manual de usuario

Ambiente Montessori digital para niños de 3 a 6 años: 78 materiales — 73 con
100 niveles cada uno (7 300 en total) y 5 actividades libres, sin niveles ni
puntaje, entre ellas una pizarra de dibujo libre. Todo en español, sin
conexión, sin cuentas, sin publicidad y sin enviar un solo dato fuera del
dispositivo.

## 1. Qué es

Mi Ambiente es una réplica digital de un salón Montessori. Corre como app web
y se instala como APK de Android para usarse sin internet. No es "otra app de
juegos": cada decisión de diseño viene de la pedagogía Montessori, no de
mecánicas de videojuego.

## 2. Qué la hace Montessori

- **Control del error.** Corrige el material, no la app. No hay "perdiste" ni
  cruces rojas: si la pieza no es la que toca, vuelve al canasto y aparece
  "Esa todavía no. Busca otra".
- **Lección de tres periodos.** Los materiales de nomenclatura recorren
  nombrar → reconocer → evocar a lo largo de los 100 niveles, con el modelo
  oculto en las últimas etapas.
- **Aislar la dificultad.** Cada nivel cambia una sola variable. La torre
  rosa no se "acelera": primero pierde el número de apoyo, luego los tamaños
  se acercan, luego se invierte la serie.
- **Vocabulario que crece.** El orden de presentación (p. ej. el triángulo
  escaleno hasta cerca del nivel 90) vive en los datos, no se improvisa.
- **Fonética, no nombres de letras.** La app dice "mmm", no "eme".
- **Repetir no es retroceder.** Las estrellas se dan una sola vez por nivel.
- **Ambiente preparado, también visual.** Paleta de papel, madera y lino;
  "modo calma" quita fondo animado y confeti.
- **Inglés mínimo, a propósito.** Treinta palabras concretas, sin gramática.

## 3. Las áreas y los 78 materiales

| Área | Materiales | Qué desarrolla |
|---|---|---|
| 🫗 Vida práctica | 9 | Coordinación, orden, concentración, independencia |
| 🔴 Sensorial | 17 | Refinar los sentidos: tamaño, forma, color, sonido, textura, temperatura, peso, sabor |
| ✍️ Lenguaje | 12 | Del sonido a la letra, y de la letra a la palabra |
| 🔢 Matemáticas | 10 | Cantidad concreta antes que número abstracto |
| 🌍 Cultura y naturaleza | 14 | El mundo, los seres vivos y su clasificación |
| 🎨 Expresión libre | 3 | Crear sin consigna, sin puntaje y sin prisa |
| 🤝 Juegos en compañía | 5 | Turnos, gracia y cortesía |
| 🤸 Movimiento | 8 | Control del cuerpo y coordinación ojo-mano |

Todas las áreas superan ya el rango de variedad que se acordó como objetivo
inicial (ver `RECOVERY.md`, sección "Roadmap activo").

Catálogo completo con slug, edad orientativa y objetivo pedagógico de cada
material: `data/games.ts`.

Materiales agregados para acercar la variedad de la app a la de las apps
Montessori comerciales:

- **El ciclo de la mariposa** (`ciclo-vida`, cultura) — ordenar huevo,
  oruga, crisálida y mariposa; seriación por tiempo, no por tamaño.
- **Formas de tierra y agua** (`tierra-agua`, cultura) — isla, lago,
  península, cabo, golfo, estrecho, istmo, archipiélago.
- **El sistema solar** (`sistema-solar`, cultura) — ordenar los ocho
  planetas por distancia al Sol.
- **Partes de la planta** (`partes-planta`, cultura) — raíz, tallo, hoja,
  flor y fruto.
- **Cuenta las sílabas** (`silabas`, lenguaje) — clasificar palabras por sus
  golpes de voz, conciencia fonológica previa a la letra.
- **El o la** (`el-la`, lenguaje) — reconocer el género gramatical de un
  sustantivo.
- **Pinza de transferencia** (`pinza`, vida práctica) — mover objetos de
  uno en uno; tomar uno de más es el error.
- **Los husos** (`husos`, matemáticas) — corresponder cantidad con número
  del 0 al 9, incluido el cero.
- **Doblar la tela** (`doblar`, vida práctica) — extendida, a la mitad, en
  cuarto, guardada.
- **Pares e impares** (`pares-impares`, matemáticas) — ¿la cantidad se
  reparte en parejas exactas?
- **Xilófono** (`xilofono`, expresión libre) — instrumento libre; cada
  barra es el color de un área del ambiente.
- **Áspero o liso** (`textura`, sensorial) — las clásicas tablillas
  rugosas y lisas.
- **Mayúsculas y minúsculas** (`mayusculas`, lenguaje) — reconocer las dos
  formas de una misma letra.
- **¿Cuántos lados tiene?** (`lados`, matemáticas) — clasifica las figuras
  del gabinete de geometría por su número de lados.
- **Los cinco sentidos** (`sentidos`, cultura) — vista, oído, olfato,
  gusto y tacto.
- **Día y noche** (`dia-noche`, cultura) — ¿es de día o de noche?
- **Mitades y enteros** (`mitades`, matemáticas) — primer contacto con la
  fracción, siempre concreto.
- **Palabras que riman** (`rimas`, lenguaje) — dos familias fijas de rima.
- **Singular y plural** (`singular-plural`, lenguaje) — la palabra junto a
  su cantidad concreta.
- **Banderas del mundo** (`banderas`, cultura) — seis banderas dibujadas
  planas.
- **Partes del cuerpo** (`cuerpo`, cultura) — cabeza, ojo, mano, brazo,
  pierna y pie.
- **El ciclo del agua** (`ciclo-agua`, cultura) — sol, nube, lluvia, río.
- **Instrumentos musicales** (`instrumentos`, sensorial) — nomenclatura.
- **Caliente o frío** (`temperatura`, sensorial) — sentido térmico.
- **Grande, mediano o chico** (`tamanos`, vida práctica) — clasificación
  en tres canastas.
- **¿Qué hora es?** (`reloj`, matemáticas) — las doce horas en punto.
- **¿Qué come?** (`dieta-animal`, cultura) — herbívoro, carnívoro u
  omnívoro.
- **Estados del agua** (`estados-agua`, cultura) — sólido, líquido o gas.
- **Pesado o ligero** (`peso`, sensorial) — sentido bárico.
- **Dulce o salado** (`sabor`, sensorial) — sentido gustativo.
- **¿Qué me pongo?** (`clima`, vida práctica) — vestirse según el clima.
- **La rutina de la mañana** (`rutina`, vida práctica) — despertar,
  vestirse, desayunar, ir a la escuela.

Cinco actividades son libres a propósito y no tienen niveles: la pizarra, el
juego del silencio, los marcos de vestir, el dado de retos y el xilófono.

## 4. La pizarra (`/pizarra`)

Lienzo a pantalla completa para dibujar con el dedo: ocho herramientas
(lápiz, crayón con textura, marcador translúcido, neón, aerosol, cubeta de
relleno, sellos, borrador), grosor variable por presión, modo mandala
(simetría radial de 2/4/6/8 ejes), siete hojas, guías punteadas de letras,
números y formas, deshacer/rehacer, galería local de 12 dibujos y
compartir/descargar PNG.

## 5. Los 100 niveles

Cada material recorre 10 etapas de 10 niveles (`lib/levels.ts`). Dentro de
una etapa la dificultad casi no cambia; al saltar de etapa entra una
variable nueva. Las etapas tienen nombre: 🌱 Primeros pasos, 🌿 Ya lo
entiendo… hasta 👑 Maestro.

## 6. La vista "Mamá y papá" (`/padres`)

Ventana para entender qué está trabajando el niño, sin ranking ni
comparación con otros niños:

- Resumen: niveles logrados, estrellas, materiales tocados, veces jugado.
- Nombre del niño (para saludarlo al entrar).
- Interruptores: sonidos, voz, modo calma.
- Borrar progreso, con confirmación.

## 7. Instalar el APK en una tablet

Archivo: `android/app/build/outputs/apk/debug/app-debug.apk`. Requiere
Android 7.0+.

**Opción A — copiar el archivo:**

1. Copia el APK a la tablet (USB, Drive, WhatsApp, correo).
2. Ábrelo desde el explorador de archivos (carpeta *Descargas*).
3. Activa "Permitir instalar de esta fuente" cuando Android lo pida.
4. Toca **Instalar**. Si Play Protect avisa, elige **Instalar de todos
   modos** (normal: no viene de la tienda).
5. Aparece como **Mi Ambiente** 🦉.

**Opción B — por cable con adb** (con depuración USB activada):

```bash
adb install -r android/app/build/outputs/apk/debug/app-debug.apk
```

## 8. Antes de dar la tablet al niño

En Ajustes de Android: fijar la pantalla (Seguridad → Fijar apps), brillo
automático apagado, volumen a la mitad. En la app, en "Mamá y papá": nombre,
modo calma si se distrae, y apagar la voz si la tablet no tiene voz en
español instalada.

## 9. Privacidad

La app no hace ninguna petición de red. Todo el contenido viaja dentro del
APK y el progreso se guarda solo en el dispositivo. Funciona igual en modo
avión. Declara un único permiso (`INTERNET`, sin uso real, añadido por
Capacitor por omisión). No pide cámara, micrófono, ubicación, contactos ni
almacenamiento. Sin cuentas, sin publicidad, sin analítica.

## 10. Límites honestos

- No sustituye el material real: un cubo de madera pesa, rueda y se cae.
- El progreso vive solo en el dispositivo; se borra si se desinstala la app.
- Los tiempos y tolerancias (`data/levels/`) están calibrados a ojo, no
  medidos con usuarios reales.
