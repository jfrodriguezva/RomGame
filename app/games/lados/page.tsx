"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import { LADOS_LEVELS, formasPara, type FormaConLados, type LadosLevel } from "@/data/levels/lados";

/** El contorno importa más que el relleno, igual que en el gabinete de figuras. */
function Figura({ path, size }: { path: string; size: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
      <path
        d={path}
        fill="#f3dbe3"
        stroke="#8a4b5e"
        strokeWidth={size > 60 ? 3 : 4}
        strokeLinejoin="round"
      />
    </svg>
  );
}

export default function LadosPage() {
  return (
    <MaterialClasificar<LadosLevel, FormaConLados>
      slug="lados"
      levels={LADOS_LEVELS}
      consigna={() => "¿Cuántos lados tiene?"}
      disponibles={(config) => formasPara(config.activos)}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={(config) => config.activos.map((n) => ({ clave: String(n), nombre: `${n} lados` }))}
      claveDe={(item) => String(item.lados)}
      keyDe={(item) => item.forma.id}
      render={(item) => <Figura path={item.forma.path} size={110} />}
      renderChip={(item) => <Figura path={item.forma.path} size={28} />}
      mensajeError={(item) => `${item.forma.label} tiene ${item.lados} lados`}
    />
  );
}
