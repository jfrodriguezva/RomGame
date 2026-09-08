"use client";

import MaterialOrdenar from "@/components/MaterialOrdenar";
import { SISTEMA_SOLAR_LEVELS, planetaDe, type Planeta } from "@/data/levels/sistema-solar";

function PlanetaIcono({ p }: { p: Planeta }) {
  const vb = p.diametro * 1.7;
  const cx = vb / 2;
  const cy = vb / 2;
  const r = p.diametro / 2;
  return (
    <svg width={vb} height={vb} viewBox={`0 0 ${vb} ${vb}`} aria-hidden="true">
      {p.anillo && (
        <ellipse
          cx={cx}
          cy={cy}
          rx={r * 1.65}
          ry={r * 0.55}
          fill="none"
          stroke="#a68a4a"
          strokeWidth={3}
          transform={`rotate(-18 ${cx} ${cy})`}
        />
      )}
      <circle cx={cx} cy={cy} r={r} fill={p.color} />
    </svg>
  );
}

export default function SistemaSolarPage() {
  return (
    <MaterialOrdenar
      slug="sistema-solar"
      levels={SISTEMA_SOLAR_LEVELS}
      consigna="Ordena los planetas desde el Sol"
      consignaInvertida="Ordena los planetas desde el Sol"
      render={(valor, { pista }) => {
        const p = planetaDe(valor);
        return (
          <div className="flex flex-col items-center justify-center gap-1">
            <PlanetaIcono p={p} />
            {pista ? (
              <span className="text-[10px] font-bold text-stone-500">{p.nombre}</span>
            ) : null}
          </div>
        );
      }}
    />
  );
}
