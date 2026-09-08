"use client";

import MaterialTransferir from "@/components/MaterialTransferir";
import { PINZA_LEVELS, OBJETOS_PINZA, type PinzaLevel } from "@/data/levels/pinza";

export default function PinzaPage() {
  return (
    <MaterialTransferir<PinzaLevel>
      slug="pinza"
      levels={PINZA_LEVELS}
      origenInicial={(config) => config.origen}
      consigna={(config) => `Transfiere ${config.objetivo} de una en una`}
      renderPieza={(config) => (
        // El emoji cambia por nivel para que no siempre sea el mismo objeto.
        <span className="text-3xl">{OBJETOS_PINZA[config.level % OBJETOS_PINZA.length]}</span>
      )}
    />
  );
}
