"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { ESTACIONES_LEVELS, estacionDe } from "@/data/levels/estaciones";

export default function EstacionesPage() {
  return (
    <MaterialOrdenar
      slug="estaciones"
      levels={ESTACIONES_LEVELS}
      consigna="Ordena el año: empieza por primavera"
      consignaInvertida="Ordena el año: empieza por primavera"
      render={(valor) => {
        const e = estacionDe(valor);
        return (
          <div className="flex h-20 w-20 flex-col items-center justify-center gap-1 rounded-2xl bg-white/85 shadow-sm ring-1 ring-black/5">
            <span className="text-4xl">{e.emoji}</span>
            <span className="text-[10px] font-bold text-stone-500">{e.nombre}</span>
          </div>
        );
      }}
    />
  );
}
