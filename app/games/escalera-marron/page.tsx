"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { ESCALERA_LEVELS, grosorPrisma } from "@/data/levels/escalera-marron";

export default function EscaleraMarronPage() {
  return (
    <MaterialOrdenar
      slug="escalera-marron"
      levels={ESCALERA_LEVELS}
      consigna="Pon abajo el prisma más ancho"
      consignaInvertida="Ahora al revés: del más delgado al más ancho"
      orientacion="horizontal"
      render={(valor, { pista }) => (
        <div
          className="flex items-center justify-center rounded-md text-white shadow-[inset_0_-4px_8px_rgba(0,0,0,0.15)]"
          style={{
            width: 210,
            height: grosorPrisma(valor),
            backgroundColor: "#8a6244",
            fontSize: Math.max(9, grosorPrisma(valor) / 2.4),
          }}
        >
          {pista ? <span className="font-extrabold opacity-70">{valor}</span> : null}
        </div>
      )}
    />
  );
}
