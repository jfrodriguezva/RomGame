# Mi Ambiente

Un ambiente Montessori digital para niños de 3 a 6 años: **57 materiales**, **100 niveles cada uno**
(5 700 en total) y una pizarra de dibujo libre. Todo en español, sin conexión, sin cuentas, sin
publicidad y sin enviar un solo dato fuera del dispositivo.

Corre como app web (Next.js) y se empaqueta como **APK de Android** con Capacitor.

```bash
npm install
npm run dev        # http://localhost:40000
npm run android    # genera android/app/build/outputs/apk/debug/app-debug.apk
```

---

## Qué lo hace Montessori y no "otra app de juegos"

No es un tema visual: son decisiones que cambian cómo se comporta la app.

**Control del error.** El material corrige, no la app. No hay "perdiste", ni vidas, ni cruces
rojas, ni un sonido de fracaso. Si el cubo no es el que toca, vuelve al canasto y aparece
"Esa todavía no. Busca otra". El niño descubre su error sin que nadie se lo señale.

**Lección de tres periodos.** Los materiales de nomenclatura (figuras, emociones, animales,
letras, palabras en inglés) recorren los tres periodos clásicos a lo largo de los 100 niveles:
primero el material *nombra* ("esto es el triángulo"), después pide *reconocer* ("muéstrame el
triángulo") y al final pide *evocar* ("¿qué es esto?"), con el modelo oculto en las últimas etapas.

**Aislar la dificultad.** Cada nivel introduce una variable nueva y deja las demás quietas. La
torre rosa no se vuelve "más rápida": primero se quita el número de apoyo, luego los tamaños pasan
a ser vecinos, luego se invierte la serie.

**Vocabulario que crece.** En el nivel 1 el gabinete de geometría ofrece círculo, cuadrado y
triángulo. El triángulo escaleno aparece cerca del nivel 90. El orden de presentación está en los
datos, no improvisado.

**Fonética, no nombres de letras.** La app dice "mmm", no "eme". Es la diferencia entre poder leer
"mamá" y no poder.

**Repetir no es retroceder.** Las estrellas se dan una sola vez por nivel, así que repetir no da
premio ni lo quita. Un niño puede hacer veinte veces el nivel 3: en Montessori eso es exactamente
lo que se espera.

**Ambiente preparado, también visual.** Paleta de papel, madera y lino; nada parpadea ni compite
por la atención. El "modo calma" quita hasta el fondo animado y el confeti.

**Inglés mínimo, a propósito.** Treinta palabras concretas, de tres en tres, sin gramática ni
frases. A esta edad se está construyendo la lengua materna.

---

## Las áreas

Los materiales están organizados como el ambiente real, por áreas y no por "categorías de juego".

| Área | Materiales | Qué desarrolla |
|---|---|---|
| 🫗 Vida práctica | 6 | Coordinación, orden, concentración, independencia |
| 🔴 Sensorial | 12 | Refinar los sentidos: tamaño, forma, color, sonido |
| ✍️ Lenguaje | 9 | Del sonido a la letra, y de la letra a la palabra |
| 🔢 Matemáticas | 7 | Cantidad concreta antes que número abstracto |
| 🌍 Cultura y naturaleza | 7 | El mundo, los seres vivos y su clasificación |
| 🎨 Expresión libre | 3 | Crear sin consigna, sin puntaje y sin prisa |
| 🤝 Juegos en compañía | 5 | Turnos, gracia y cortesía |
| 🤸 Movimiento | 8 | Control del cuerpo y coordinación ojo-mano |

### Materiales nuevos en esta versión

Réplicas digitales de material Montessori real:

- **Torre rosa** — diez cubos, un solo color: la única variable es el tamaño.
- **Escalera marrón** — diez prismas del mismo largo y distinto grosor.
- **Cilindros con botón** — cada cilindro entra en un solo hueco; si sobra uno, algo se hizo mal.
- **Cubo del binomio** — reproducir un patrón de colores; en las últimas etapas, de memoria.
- **Barras numéricas** — contar, nombrar, construir la escalera, sumar y completar a diez.
- **Banco dorado** — componer números de hasta cuatro cifras con perlas, barras, cuadrados y cubos.
- **Tabla del cien** — colocar hasta 22 fichas faltantes apoyándose en la decena.
- **Letras de lija** — recorrer la letra con el dedo mientras se escucha su sonido.
- **Veo veo** — encontrar el objeto que empieza (o termina) con un sonido dado.
- **Alfabeto móvil** — componer palabras con letras sueltas antes de saber escribirlas.
- **Vida práctica** — trasvasar, abotonar, verter hasta la raya y clasificar.
- **El juego del silencio** — respiración guiada y escucha; sin niveles ni puntaje.
- **Los continentes** — mapa con los colores del material, y qué animal vive en cada uno.
- **¿Vivo o no vivo?** — la primera clasificación científica: vivo/no vivo, animal/planta, camina/nada/vuela.

### Los últimos cuatro materiales

Ampliando las áreas más flacas del catálogo (cultura y naturaleza, lenguaje) para acercar la
variedad de la app a la de las aplicaciones Montessori comerciales más conocidas:

- **El ciclo de la mariposa** — ordenar huevo, oruga, crisálida y mariposa: seriación, pero por
  tiempo en vez de por tamaño.
- **Formas de tierra y agua** — isla, lago, península, cabo, golfo, estrecho, istmo y
  archipiélago, como diagramas planos de dos colores.
- **El sistema solar** — ordenar los ocho planetas por distancia al Sol.
- **Cuenta las sílabas** — clasificar palabras por sus golpes de voz, el paso antes de relacionar
  sonido con letra.
- **Partes de la planta** — raíz, tallo, hoja, flor y fruto: nomenclatura de botánica.
- **El o la** — reconocer el género gramatical de un sustantivo, escuchando y clasificando.
- **Pinza de transferencia** — mover objetos de uno en uno; tomar uno de más también es un error.
- **Los husos** — corresponder cantidad con número del 0 al 9, incluido el cero como "nada".
- **Doblar la tela** — extendida, a la mitad, en cuarto, guardada: secuencia motriz real.
- **Pares e impares** — ¿la cantidad se puede repartir en parejas exactas, o sobra una?
- **Xilófono** — instrumento libre; cada barra es el color de un área del ambiente.

---

## La pizarra

`/pizarra` — Un lienzo a pantalla completa pensado para dibujar con el dedo:

- Ocho herramientas: lápiz, crayón con textura, marcador translúcido, neón, aerosol, cubeta de
  relleno, sellos y borrador.
- Grosor variable **por presión** del dedo o del lápiz activo.
- **Modo mandala**: simetría radial de 2, 4, 6 u 8 ejes con espejo. Un garabato se convierte en
  una roseta simétrica.
- Siete hojas: papel, blanco, cuadrícula, renglones, puntos, pizarrón verde y kraft.
- **Guías punteadas** para repasar: las 27 letras, los números y doce formas.
- Deshacer y rehacer, galería local de 12 dibujos, y compartir/descargar el PNG (usa la hoja de
  compartir nativa dentro del APK, donde las descargas del WebView no funcionan).

---

## Cómo están hechos los 100 niveles

No es la misma curva estirada. `lib/levels.ts` define **10 etapas de 10 niveles**, y la dificultad
sube a saltos: dentro de una etapa apenas cambia (repetición cómoda) y al cambiar de etapa entra
una dificultad nueva.

```ts
// Memoria: de tres parejas a dieciséis, con un tramo largo y estable en medio
export const MEMORAMA_LEVELS = levels100((_, level) => ({
  pairs: phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16]),
}));
```

Cada valor del arreglo es el de una etapa; `phasedInt` interpola entre ellos. Las etapas tienen
nombre y el niño las ve en el selector: 🌱 Primeros pasos, 🌿 Ya lo entiendo, … 👑 Maestro.

Cuatro actividades son **libres a propósito** y no tienen niveles: la pizarra, el juego del
silencio, los marcos de vestir y el dado de retos. En el ambiente real tampoco los tienen.

---

## Sonido y voz sin archivos

No hay un solo `.mp3` en el repositorio y aun así la app suena:

- `lib/audio.ts` sintetiza los efectos con WebAudio sobre una escala pentatónica, con envolvente
  suave y una segunda voz ligeramente desafinada por debajo de cada nota (calidez de "coro" en vez
  de un tono de juguete solo). El sonido de "vuelve a intentar" es una nota grave y breve, nunca un
  pitido de error.
- `lib/speech.ts` usa la síntesis de voz del sistema en español, con una tabla de fonemas para
  decir el sonido de cada letra.
- `lib/haptics.ts` usa `navigator.vibrate`, que el WebView de Android soporta sin plugin nativo.

Los tres respetan los interruptores de la vista de padres.

**Textura de papel.** El fondo de cada material (`GameShell`) y de la portada lleva un ruido
fractal casi imperceptible (5% de opacidad) sobre el color plano del área: un solo punto de
aplicación en `globals.css` (clase `.textura-papel`) para que el fondo deje de ser un color liso y
se sienta más cerca de "papel" sin tocar las 57 pantallas de material una por una.

---

## Estructura

```
app/
  page.tsx              inicio, por áreas
  pizarra/              lienzo de dibujo libre
  padres/               progreso, ajustes y cómo acompañar
  admin/                editor de puntos para imágenes propias
  games/<slug>/         un material por carpeta
components/
  GameShell             marco común: encabezado, nivel, consigna, control del error
  MaterialQuiz          lección de tres periodos (5 materiales la usan)
  MaterialOrdenar       seriación (torre rosa, escalera marrón)
  TrazoGuiado           recorrer un glifo con el dedo
  LevelSelector         100 niveles en 10 etapas, con lo alcanzado y lo cerrado
data/
  games.ts              catálogo con área, edad, material y objetivo pedagógico
  levels/<slug>.ts      curva de dificultad de cada material
  trazos-letras.ts      trazos de letras y números, en el orden en que se escriben
  palabras.ts           banco de palabras cortas y fonéticas
lib/
  levels.ts             motor de etapas y curvas
  montessori.ts         áreas, tres periodos y lenguaje del control del error
  useMaterial.ts        estado común: nivel, acierto, intento, cierre
  progressStore.ts      progreso local (zustand + localStorage)
scripts/build-android.mjs
```

---

## Android

`next.config.ts` genera un export estático cuando `CAP_BUILD=1`; el resto del tiempo funciona como
servidor Next normal.

```bash
npm run export   # solo el sitio estático en out/
npm run android  # export + cap sync + gradlew assembleDebug
```

El workflow [`build-apk.yml`](.github/workflows/build-apk.yml) hace lo mismo en GitHub Actions y
sube el APK como artefacto en cada push a `main`.

El APK es de **debug**, firmado con la clave de depuración de Android: sirve para instalarlo en
dispositivos propios, no para publicar en Play Store, que exige una firma de release.

Declara un solo permiso, `INTERNET`, que Capacitor añade por omisión para su WebView. La app no
hace peticiones de red: funciona igual en modo avión.

---

## Imágenes y sonidos propios

Los materiales usan emoji y SVG generado, así que la app no incluye ningún personaje con derechos
de autor. Si quieres poner tus propias fotos, `public/images/README.md` explica dónde van, y
`/admin` genera las coordenadas para los materiales que las necesitan.

---

## Notas honestas

- Esta app **no sustituye el material real**. Un cubo de madera pesa, rueda y se cae; eso no se
  puede simular. Sirve como complemento, para el coche o la sala de espera.
- Las nuevas reglas de ESLint de React 19 marcan `setState` dentro de `useEffect` en varias
  páginas. Es un patrón que ya venía en el proyecto; no rompe el build ni la app, pero está
  pendiente de limpiar.
- Los tiempos y umbrales (tolerancias de trazo, velocidades) están calibrados a ojo. Si algo le
  queda grande o chico a tu hijo, están todos en `data/levels/`.
