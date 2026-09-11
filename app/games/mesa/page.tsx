"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { MESA_LEVELS, etapaMesa } from "@/data/levels/mesa";

export default function MesaPage() {
  return (
    <MaterialOrdenar
      slug="mesa"
      levels={MESA_LEVELS}
      consigna="Pon la mesa: empieza por el mantel"
      consignaInvertida="Pon la mesa: empieza por el mantel"
      render={(valor) => {
        const etapa = etapaMesa(valor);
        return (
          <div className="flex h-20 w-20 flex-col items-center justify-center gap-1 rounded-2xl bg-white/85 shadow-sm ring-1 ring-black/5">
            {etapa.emoji ? (
              <span className="text-4xl">{etapa.emoji}</span>
            ) : (
              <div className="h-7 w-14 rounded-md border-2 border-[#a9895a] bg-[#f3ede3]" />
            )}
            <span className="px-1 text-center text-[9px] font-bold leading-tight text-stone-500">
              {etapa.nombre}
            </span>
          </div>
        );
      }}
    />
  );
}
