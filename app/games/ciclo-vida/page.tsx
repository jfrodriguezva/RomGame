"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { CICLO_VIDA_LEVELS, etapaMariposa } from "@/data/levels/ciclo-vida";

/**
 * No existe un emoji estándar de crisálida, así que se dibuja a mano con el
 * mismo criterio del resto de la app: forma plana, un solo color, sin
 * personaje con derechos de autor.
 */
function Crisalida({ size }: { size: number }) {
  return (
    <svg width={size} height={size * 1.3} viewBox="0 0 40 52" fill="none" aria-hidden="true">
      <line x1="20" y1="0" x2="20" y2="8" stroke="#8a6a44" strokeWidth="2" />
      <path
        d="M20 8c10 0 15 9 15 20s-6 22-15 22S5 39 5 28 10 8 20 8Z"
        fill="#c9a06a"
        stroke="#8a6a44"
        strokeWidth="1.5"
      />
      <path
        d="M8 20c9 3 15 3 24 0M7 30c10 3 16 3 26 0M8 40c9 3 15 3 24 0"
        stroke="#8a6a44"
        strokeWidth="1.5"
        strokeLinecap="round"
        opacity="0.55"
      />
    </svg>
  );
}

export default function CicloVidaPage() {
  return (
    <MaterialOrdenar
      slug="ciclo-vida"
      levels={CICLO_VIDA_LEVELS}
      consigna="Ordena el ciclo: empieza por el huevo"
      consignaInvertida="Ordena el ciclo: empieza por el huevo"
      render={(valor, { pista }) => {
        const etapa = etapaMariposa(valor);
        return (
          <div
            className="flex h-20 w-20 flex-col items-center justify-center gap-1 rounded-2xl bg-white/85 shadow-sm ring-1 ring-black/5"
            aria-label={etapa.nombre}
          >
            <span className="flex h-11 items-center justify-center text-4xl">
              {etapa.valor === 1 && "🥚"}
              {etapa.valor === 2 && "🐛"}
              {etapa.valor === 3 && <Crisalida size={34} />}
              {etapa.valor === 4 && "🦋"}
            </span>
            {pista ? (
              <span className="text-[10px] font-bold text-stone-500">{etapa.nombre}</span>
            ) : null}
          </div>
        );
      }}
    />
  );
}
