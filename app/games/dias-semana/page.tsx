"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { DIAS_SEMANA_LEVELS, diaSemana } from "@/data/levels/dias-semana";

export default function DiasSemanaPage() {
  return (
    <MaterialOrdenar
      slug="dias-semana"
      levels={DIAS_SEMANA_LEVELS}
      consigna="Ordena la semana: empieza por lunes"
      consignaInvertida="Ordena la semana: empieza por lunes"
      render={(valor) => (
        <div className="flex h-16 w-24 items-center justify-center rounded-2xl bg-white/85 px-2 shadow-sm ring-1 ring-black/5">
          <span className="text-center text-sm font-extrabold text-stone-700">
            {diaSemana(valor).nombre}
          </span>
        </div>
      )}
    />
  );
}
