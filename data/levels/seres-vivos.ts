import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * Clasificación: la primera ciencia.
 *
 * Antes de saber qué es un vertebrado, el niño necesita hacer montoncitos:
 * esto vive, esto no. Después: esto es planta, esto es animal. Y más tarde:
 * esto vuela, esto nada, esto camina. Siempre lo mismo, cada vez más fino.
 */
export interface SerDef {
  emoji: string;
  nombre: string;
  vivo: boolean;
  tipo: "planta" | "animal" | "objeto";
  medio?: "tierra" | "agua" | "aire";
}

export const SERES: SerDef[] = [
  { emoji: "🐶", nombre: "perro", vivo: true, tipo: "animal", medio: "tierra" },
  { emoji: "🐈", nombre: "gato", vivo: true, tipo: "animal", medio: "tierra" },
  { emoji: "🐘", nombre: "elefante", vivo: true, tipo: "animal", medio: "tierra" },
  { emoji: "🐇", nombre: "conejo", vivo: true, tipo: "animal", medio: "tierra" },
  { emoji: "🐢", nombre: "tortuga", vivo: true, tipo: "animal", medio: "tierra" },
  { emoji: "🐝", nombre: "abeja", vivo: true, tipo: "animal", medio: "aire" },
  { emoji: "🦅", nombre: "águila", vivo: true, tipo: "animal", medio: "aire" },
  { emoji: "🦋", nombre: "mariposa", vivo: true, tipo: "animal", medio: "aire" },
  { emoji: "🦜", nombre: "loro", vivo: true, tipo: "animal", medio: "aire" },
  { emoji: "🐟", nombre: "pez", vivo: true, tipo: "animal", medio: "agua" },
  { emoji: "🐬", nombre: "delfín", vivo: true, tipo: "animal", medio: "agua" },
  { emoji: "🐙", nombre: "pulpo", vivo: true, tipo: "animal", medio: "agua" },
  { emoji: "🦈", nombre: "tiburón", vivo: true, tipo: "animal", medio: "agua" },

  { emoji: "🌳", nombre: "árbol", vivo: true, tipo: "planta", medio: "tierra" },
  { emoji: "🌻", nombre: "girasol", vivo: true, tipo: "planta", medio: "tierra" },
  { emoji: "🌵", nombre: "cactus", vivo: true, tipo: "planta", medio: "tierra" },
  { emoji: "🍄", nombre: "hongo", vivo: true, tipo: "planta", medio: "tierra" },
  { emoji: "🌿", nombre: "hierba", vivo: true, tipo: "planta", medio: "tierra" },
  { emoji: "🌷", nombre: "tulipán", vivo: true, tipo: "planta", medio: "tierra" },
  { emoji: "🪴", nombre: "planta", vivo: true, tipo: "planta", medio: "tierra" },

  { emoji: "🪑", nombre: "silla", vivo: false, tipo: "objeto" },
  { emoji: "🚗", nombre: "coche", vivo: false, tipo: "objeto" },
  { emoji: "⏰", nombre: "reloj", vivo: false, tipo: "objeto" },
  { emoji: "📕", nombre: "libro", vivo: false, tipo: "objeto" },
  { emoji: "🥄", nombre: "cuchara", vivo: false, tipo: "objeto" },
  { emoji: "🪨", nombre: "piedra", vivo: false, tipo: "objeto" },
  { emoji: "🧸", nombre: "peluche", vivo: false, tipo: "objeto" },
  { emoji: "🚲", nombre: "bicicleta", vivo: false, tipo: "objeto" },
  { emoji: "🔑", nombre: "llave", vivo: false, tipo: "objeto" },
  { emoji: "👟", nombre: "zapato", vivo: false, tipo: "objeto" },
  { emoji: "💡", nombre: "foco", vivo: false, tipo: "objeto" },
  { emoji: "🪁", nombre: "papalote", vivo: false, tipo: "objeto" },
];

export type CriterioSeres = "vivo" | "reino" | "medio";

export interface SeresLevel {
  level: number;
  criterio: CriterioSeres;
  /** Cuántos elementos hay que clasificar en el nivel. */
  cantidad: number;
}

export const SERES_LEVELS: SeresLevel[] = levels100((_, level) => {
  const etapa = stageOf(level);
  return {
    criterio: etapa <= 4 ? "vivo" : etapa <= 7 ? "reino" : "medio",
    cantidad: phasedInt(level, [4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15]),
  };
});

export const CANASTAS: Record<CriterioSeres, Array<{ clave: string; nombre: string; emoji: string }>> = {
  vivo: [
    { clave: "si", nombre: "Está vivo", emoji: "🌱" },
    { clave: "no", nombre: "No está vivo", emoji: "🪨" },
  ],
  reino: [
    { clave: "animal", nombre: "Animal", emoji: "🐾" },
    { clave: "planta", nombre: "Planta", emoji: "🌿" },
  ],
  medio: [
    { clave: "tierra", nombre: "Camina", emoji: "🐾" },
    { clave: "agua", nombre: "Nada", emoji: "🌊" },
    { clave: "aire", nombre: "Vuela", emoji: "🪶" },
  ],
};

/** A qué canasta pertenece un ser según el criterio del nivel. */
export function canastaDe(ser: SerDef, criterio: CriterioSeres): string {
  if (criterio === "vivo") return ser.vivo ? "si" : "no";
  if (criterio === "reino") return ser.tipo;
  return ser.medio ?? "tierra";
}

/** Elementos válidos para un criterio (el de medios excluye los objetos). */
export function seresPara(criterio: CriterioSeres): SerDef[] {
  if (criterio === "vivo") return SERES;
  if (criterio === "reino") return SERES.filter((s) => s.tipo !== "objeto");
  return SERES.filter((s) => s.tipo === "animal");
}
