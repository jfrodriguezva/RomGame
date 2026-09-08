"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  CANASTAS,
  SERES_LEVELS,
  type SerDef,
  type SeresLevel,
  canastaDe,
  seresPara,
} from "@/data/levels/seres-vivos";

const CONSIGNAS: Record<SeresLevel["criterio"], string> = {
  vivo: "¿Está vivo o no está vivo?",
  reino: "¿Es un animal o una planta?",
  medio: "¿Camina, nada o vuela?",
};

const COLOR_CULTURA = {
  fondo: "bg-[#d6eae5]",
  texto: "text-[#31665c]",
  chip: "text-[#2c5c53]",
};

export default function SeresVivosPage() {
  return (
    <MaterialClasificar<SeresLevel, SerDef>
      slug="seres-vivos"
      levels={SERES_LEVELS}
      consigna={(config) => CONSIGNAS[config.criterio]}
      disponibles={(config) => seresPara(config.criterio)}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={(config) => CANASTAS[config.criterio]}
      claveDe={(item, config) => canastaDe(item, config.criterio)}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `El ${item.nombre} va en otra canasta`}
      colorCanasta={COLOR_CULTURA}
    />
  );
}
