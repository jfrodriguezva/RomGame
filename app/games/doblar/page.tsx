"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { DOBLAR_LEVELS, etapaDoblado } from "@/data/levels/doblar";

export default function DoblarPage() {
  return (
    <MaterialOrdenar
      slug="doblar"
      levels={DOBLAR_LEVELS}
      consigna="Dobla la tela: empieza extendida"
      consignaInvertida="Dobla la tela: empieza extendida"
      render={(valor, { pista }) => {
        const etapa = etapaDoblado(valor);
        return (
          <div className="flex flex-col items-center justify-center gap-1">
            <div
              className="rounded-md border-2 border-[#a9895a] bg-[#f3ede3]"
              style={{ width: etapa.ancho, height: etapa.alto }}
            />
            {pista ? (
              <span className="text-[10px] font-bold text-stone-500">{etapa.nombre}</span>
            ) : null}
          </div>
        );
      }}
    />
  );
}
