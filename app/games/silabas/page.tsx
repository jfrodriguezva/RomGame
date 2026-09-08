"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  CANASTAS_SILABAS,
  SILABAS_LEVELS,
  type PalabraSilabas,
  type SilabasLevel,
  palabrasPara,
} from "@/data/levels/silabas";
import { hablar } from "@/lib/speech";

export default function SilabasPage() {
  return (
    <MaterialClasificar<SilabasLevel, PalabraSilabas>
      slug="silabas"
      levels={SILABAS_LEVELS}
      consigna={() => "¿Cuántas sílabas tiene?"}
      disponibles={(config) => palabrasPara(config.criterio)}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={(config) =>
        CANASTAS_SILABAS[config.criterio].map((n) => ({
          clave: String(n),
          nombre: n === 1 ? "1 sílaba" : `${n} sílabas`,
        }))
      }
      claveDe={(item) => String(item.silabas)}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre}: cuenta otra vez`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
