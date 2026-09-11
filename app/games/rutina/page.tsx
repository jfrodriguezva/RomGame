"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { RUTINA_LEVELS, etapaRutina } from "@/data/levels/rutina";

export default function RutinaPage() {
  return (
    <MaterialOrdenar
      slug="rutina"
      levels={RUTINA_LEVELS}
      consigna="Ordena la mañana: empieza por despertar"
      consignaInvertida="Ordena la mañana: empieza por despertar"
      render={(valor) => {
        const etapa = etapaRutina(valor);
        return (
          <div className="flex h-20 w-20 flex-col items-center justify-center gap-1 rounded-2xl bg-white/85 shadow-sm ring-1 ring-black/5">
            <span className="text-4xl">{etapa.emoji}</span>
            <span className="px-1 text-center text-[9px] font-bold leading-tight text-stone-500">
              {etapa.nombre}
            </span>
          </div>
        );
      }}
    />
  );
}
