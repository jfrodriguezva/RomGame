/** ¿Qué hora es? Las doce horas en punto, nomenclatura vía MaterialQuiz. */
const NUM_PALABRA = [
  "una", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez", "once", "doce",
];

export interface HoraDef {
  id: string;
  label: string;
  hora: number;
}

export const HORAS: HoraDef[] = Array.from({ length: 12 }, (_, i) => {
  const hora = i + 1;
  const palabra = NUM_PALABRA[i];
  return { id: `h${hora}`, label: hora === 1 ? "La una" : `Las ${palabra}`, hora };
});
