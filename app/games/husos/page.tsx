"use client";

import MaterialTransferir from "@/components/MaterialTransferir";
import { HUSOS_LEVELS, type HusosLevel } from "@/data/levels/husos";

const POOL = 9;

export default function HusosPage() {
  return (
    <MaterialTransferir<HusosLevel>
      slug="husos"
      levels={HUSOS_LEVELS}
      origenInicial={() => POOL}
      consigna={(config) => `Pon los husos del ${config.objetivo}`}
      mensajeError={(config) =>
        config.objetivo === 0 ? "El 0 no lleva ningún huso" : "Ya son suficientes. Cuenta otra vez"
      }
      renderPieza={() => <span className="text-2xl">🥢</span>}
      etiquetaDestinoVacio="Vacío"
      extra={(config, destino, completar) => (
        <div className="mb-4 flex flex-col items-center gap-2">
          <span className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white text-3xl font-extrabold text-stone-600 ring-1 ring-black/5">
            {config.objetivo}
          </span>
          {config.objetivo === 0 && destino.length === 0 && (
            <button
              onClick={completar}
              className="rounded-2xl bg-emerald-100 px-4 py-2 text-sm font-bold text-emerald-700 active:scale-95"
            >
              Listo, no lleva ninguno
            </button>
          )}
        </div>
      )}
    />
  );
}
