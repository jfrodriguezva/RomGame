"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { TORRE_LEVELS, ladoCubo } from "@/data/levels/torre-rosa";

export default function TorreRosaPage() {
  return (
    <MaterialOrdenar
      slug="torre-rosa"
      levels={TORRE_LEVELS}
      consigna="Pon abajo el cubo más grande"
      consignaInvertida="Ahora al revés: empieza por el más chiquito"
      render={(valor, { pista }) => {
        const lado = ladoCubo(valor);
        return (
          <div
            className="flex items-center justify-center rounded-md text-white shadow-[inset_0_-6px_10px_rgba(0,0,0,0.12)]"
            style={{
              width: lado,
              height: lado,
              backgroundColor: "#e59aae",
              fontSize: Math.max(10, lado / 4),
            }}
          >
            {pista ? <span className="font-extrabold opacity-70">{valor}</span> : null}
          </div>
        );
      }}
    />
  );
}
