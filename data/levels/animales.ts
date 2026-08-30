import { QUIZ_LEVELS, type QuizLevel } from "./quiz";

/**
 * Zoología: el animal, su nombre y el sonido que hace.
 *
 * Las familias son hábitats, así que en las etapas altas el material pregunta
 * dentro de un mismo grupo (todos los del mar, todos los de la granja), donde
 * las diferencias son más sutiles.
 */
export type Habitat = "granja" | "casa" | "selva" | "mar" | "bosque" | "aves" | "insectos";

export interface AnimalDef {
  name: string;
  emoji: string;
  sound: string;
  habitat: Habitat;
}

export const ANIMALS: AnimalDef[] = [
  { name: "Perro", emoji: "🐶", sound: "guau guau", habitat: "casa" },
  { name: "Gato", emoji: "🐱", sound: "miau", habitat: "casa" },
  { name: "Vaca", emoji: "🐄", sound: "muuu", habitat: "granja" },
  { name: "Pato", emoji: "🦆", sound: "cuac cuac", habitat: "aves" },
  { name: "León", emoji: "🦁", sound: "roar", habitat: "selva" },
  { name: "Caballo", emoji: "🐴", sound: "ihaaa", habitat: "granja" },
  { name: "Cerdo", emoji: "🐷", sound: "oink oink", habitat: "granja" },
  { name: "Oveja", emoji: "🐑", sound: "beee", habitat: "granja" },
  { name: "Rana", emoji: "🐸", sound: "croac croac", habitat: "insectos" },
  { name: "Abeja", emoji: "🐝", sound: "bzzzz", habitat: "insectos" },
  { name: "Elefante", emoji: "🐘", sound: "pruuum", habitat: "selva" },
  { name: "Mono", emoji: "🐵", sound: "uh uh ah ah", habitat: "selva" },
  { name: "Pollito", emoji: "🐥", sound: "pío pío", habitat: "granja" },
  { name: "Gallo", emoji: "🐓", sound: "kikirikí", habitat: "granja" },
  { name: "Conejo", emoji: "🐰", sound: "no hace ruido", habitat: "casa" },
  { name: "Búho", emoji: "🦉", sound: "uhú uhú", habitat: "aves" },
  { name: "Oso", emoji: "🐻", sound: "grooar", habitat: "bosque" },
  { name: "Tigre", emoji: "🐯", sound: "grrr", habitat: "selva" },
  { name: "Ratón", emoji: "🐭", sound: "iii iii", habitat: "casa" },
  { name: "Jirafa", emoji: "🦒", sound: "casi no hace ruido", habitat: "selva" },
  { name: "Pez", emoji: "🐟", sound: "burbujas", habitat: "mar" },
  { name: "Ballena", emoji: "🐳", sound: "un canto largo", habitat: "mar" },
  { name: "Delfín", emoji: "🐬", sound: "clic clic", habitat: "mar" },
  { name: "Pingüino", emoji: "🐧", sound: "cua cua", habitat: "aves" },
  { name: "Lobo", emoji: "🐺", sound: "auuuu", habitat: "bosque" },
  { name: "Zorro", emoji: "🦊", sound: "yip yip", habitat: "bosque" },
  { name: "Serpiente", emoji: "🐍", sound: "ssss", habitat: "selva" },
  { name: "Cocodrilo", emoji: "🐊", sound: "clac clac", habitat: "selva" },
  { name: "Ardilla", emoji: "🐿️", sound: "chit chit", habitat: "bosque" },
  { name: "Loro", emoji: "🦜", sound: "hola hola", habitat: "aves" },
  { name: "Foca", emoji: "🦭", sound: "auu auu", habitat: "mar" },
  { name: "Cangrejo", emoji: "🦀", sound: "clac", habitat: "mar" },
  { name: "Ciervo", emoji: "🦌", sound: "bramido", habitat: "bosque" },
  { name: "Pájaro", emoji: "🐦", sound: "pí pí", habitat: "aves" },
  { name: "Mariposa", emoji: "🦋", sound: "vuela en silencio", habitat: "insectos" },
  { name: "Grillo", emoji: "🦗", sound: "cri cri", habitat: "insectos" },
  { name: "Cabra", emoji: "🐐", sound: "meee", habitat: "granja" },
  { name: "Burro", emoji: "🫏", sound: "iaaa", habitat: "granja" },
  { name: "Hámster", emoji: "🐹", sound: "chiii", habitat: "casa" },
];

export type AnimalesLevel = QuizLevel;
export const ANIMALES_LEVELS: AnimalesLevel[] = QUIZ_LEVELS;
