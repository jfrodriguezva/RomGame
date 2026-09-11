"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { CICLO_AGUA_LEVELS, etapaAgua } from "@/data/levels/ciclo-agua";

export default function CicloAguaPage() {
  return (
    <MaterialOrdenar
      slug="ciclo-agua"
      levels={CICLO_AGUA_LEVELS}
      consigna="Ordena el ciclo: empieza por el sol"
      consignaInvertida="Ordena el ciclo: empieza por el sol"
      render={(valor) => {
        const etapa = etapaAgua(valor);
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
