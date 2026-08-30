/**
 * Banco de palabras.
 *
 * Todas son cortas, concretas y fonéticas: se escriben como suenan, sin
 * letras mudas ni combinaciones difíciles. Son las palabras con las que un
 * niño puede componer su primera palabra escrita usando solo los sonidos que
 * ya conoce.
 */

export interface Palabra {
  palabra: string;
  emoji: string;
  /** Número de letras; se usa para escalonar la dificultad. */
  largo: number;
}

function p(palabra: string, emoji: string): Palabra {
  return { palabra, emoji, largo: palabra.length };
}

export const PALABRAS: Palabra[] = [
  // Tres letras
  p("sol", "☀️"),
  p("pan", "🍞"),
  p("pez", "🐟"),
  p("pie", "🦶"),
  p("ojo", "👁️"),
  p("oso", "🐻"),
  p("ala", "🪽"),
  p("uva", "🍇"),
  p("sal", "🧂"),
  p("mar", "🌊"),
  p("luz", "💡"),
  p("rey", "🤴"),
  p("flor", "🌸"),

  // Cuatro letras
  p("luna", "🌙"),
  p("gato", "🐱"),
  p("casa", "🏠"),
  p("mesa", "🪑"),
  p("pato", "🦆"),
  p("dedo", "🫵"),
  p("mano", "✋"),
  p("rana", "🐸"),
  p("sopa", "🍲"),
  p("taza", "☕"),
  p("mono", "🐵"),
  p("nube", "☁️"),
  p("rosa", "🌹"),
  p("sapo", "🐸"),
  p("vela", "🕯️"),
  p("foca", "🦭"),
  p("jugo", "🧃"),
  p("lobo", "🐺"),
  p("mapa", "🗺️"),
  p("nido", "🪺"),
  p("dado", "🎲"),
  p("pino", "🌲"),
  p("bota", "🥾"),
  p("cama", "🛏️"),
  p("hoja", "🍃"),
  p("kiwi", "🥝"),
  p("lima", "🍋"),
  p("nariz", "👃"),

  // Cinco letras
  p("perro", "🐶"),
  p("leche", "🥛"),
  p("libro", "📕"),
  p("silla", "🪑"),
  p("tren", "🚂"),
  p("queso", "🧀"),
  p("globo", "🎈"),
  p("piano", "🎹"),
  p("barco", "⛵"),
  p("nieve", "❄️"),
  p("fuego", "🔥"),
  p("reloj", "⏰"),
  p("plato", "🍽️"),
  p("tigre", "🐯"),

  // Seis letras
  p("camisa", "👕"),
  p("zapato", "👟"),
  p("cebolla", "🧅"),
  p("ventana", "🪟"),
  p("caballo", "🐴"),
  p("manzana", "🍎"),
  p("tortuga", "🐢"),
  p("elefante", "🐘"),
];

/** Sonido con el que empieza la palabra. */
export function inicialDe(palabra: string): string {
  return palabra[0].toUpperCase();
}

/** Palabras que empiezan con una letra dada. */
export function palabrasCon(letra: string): Palabra[] {
  return PALABRAS.filter((p) => inicialDe(p.palabra) === letra.toUpperCase());
}

/** Letras que tienen al menos dos palabras en el banco. */
export const LETRAS_CON_PALABRAS = Array.from(
  new Set(PALABRAS.map((p) => inicialDe(p.palabra)))
).filter((l) => palabrasCon(l).length >= 2);
