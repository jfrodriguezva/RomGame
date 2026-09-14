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
              className="relative rounded-md border-2 border-[#a9895a] shadow-sm"
              style={{
                width: etapa.ancho,
                height: etapa.alto,
                // Cuadros como un mantel: sin esto era un rectángulo liso
                // indistinguible de una ficha, nada evocaba "tela".
                backgroundColor: "#f3ede3",
                backgroundImage:
                  "repeating-linear-gradient(#e3d4b8 0 5px, transparent 5px 11px), repeating-linear-gradient(90deg, #e3d4b8 0 5px, transparent 5px 11px)",
                // La sombra en dos capas hace ver que hay tela apilada
                // debajo, no solo una figura plana encogiéndose.
                boxShadow:
                  valor >= 2
                    ? `2px 2px 0 0 #e3d4b8${valor >= 3 ? ", 4px 4px 0 0 #d8c39a" : ""}`
                    : undefined,
              }}
            >
              {valor >= 2 && (
                <div className="absolute inset-y-0 left-1/2 w-[2px] -translate-x-1/2 bg-[#a9895a]/60" />
              )}
              {valor >= 3 && (
                <div className="absolute inset-x-0 top-1/2 h-[2px] -translate-y-1/2 bg-[#a9895a]/60" />
              )}
            </div>
            {pista ? (
              <span className="text-[10px] font-bold text-stone-500">{etapa.nombre}</span>
            ) : null}
          </div>
        );
      }}
    />
  );
}
