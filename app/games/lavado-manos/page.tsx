"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { LAVADO_MANOS_LEVELS, etapaLavado } from "@/data/levels/lavado-manos";

export default function LavadoManosPage() {
  return (
    <MaterialOrdenar
      slug="lavado-manos"
      levels={LAVADO_MANOS_LEVELS}
      consigna="Lávate las manos: empieza por mojar"
      consignaInvertida="Lávate las manos: empieza por mojar"
      render={(valor) => {
        const etapa = etapaLavado(valor);
        return (
          <div className="flex h-20 w-20 flex-col items-center justify-center gap-1 rounded-2xl bg-white/85 shadow-sm ring-1 ring-black/5">
            <span className="text-4xl">{etapa.emoji}</span>
            <span className="text-[10px] font-bold text-stone-500">{etapa.nombre}</span>
          </div>
        );
      }}
    />
  );
}
