import { levels100, phasedInt, stageOf } from "@/lib/levels";
import { periodoDeNivel, type Periodo } from "@/lib/montessori";

/**
 * Curva compartida por los materiales de nomenclatura (formas, emociones,
 * animales, letras, sombras, palabras en inglés).
 *
 * Los 100 niveles no son "lo mismo pero más rápido": recorren la lección de
 * tres periodos y van sumando dificultades de una en una.
 *
 *   Etapas 1-2   presentación: el material nombra antes de preguntar
 *   Etapas 3-6   reconocimiento: "muéstrame el triángulo", cada vez con más opciones
 *   Etapas 7-10  evocación: "¿qué es esto?", y al final sin ver el modelo
 */
export interface QuizLevel {
  level: number;
  /** Cuántas opciones aparecen en pantalla (2 a 8). */
  opciones: number;
  /** Cuántos aciertos completan el nivel. */
  rondas: number;
  periodo: Periodo;
  /** El modelo se esconde tras unos segundos: obliga a recordar. */
  ocultar: boolean;
  /** Las opciones se parecen entre sí (misma familia): discriminación fina. */
  parecidas: boolean;
}

export function nivelesQuiz(): QuizLevel[] {
  return levels100<QuizLevel>((_, level) => {
    const etapa = stageOf(level);
    return {
      // 11 tramos: uno por etapa más el cierre.
      opciones: phasedInt(level, [2, 3, 3, 4, 4, 5, 6, 6, 7, 8, 8]),
      rondas: phasedInt(level, [3, 3, 4, 4, 5, 5, 6, 7, 8, 9, 10]),
      periodo: periodoDeNivel(level),
      ocultar: etapa >= 9,
      parecidas: etapa >= 5,
    };
  });
}

export const QUIZ_LEVELS = nivelesQuiz();
