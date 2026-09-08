"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  EL_LA_LEVELS,
  PALABRAS_GENERO,
  type PalabraGenero,
  type ElLaLevel,
} from "@/data/levels/el-la";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "el", nombre: "El" },
  { clave: "la", nombre: "La" },
];

export default function ElLaPage() {
  return (
    <MaterialClasificar<ElLaLevel, PalabraGenero>
      slug="el-la"
      levels={EL_LA_LEVELS}
      consigna={() => "¿Se dice el o la?"}
      disponibles={() => PALABRAS_GENERO}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.articulo}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `Se dice "${item.articulo} ${item.nombre}"`}
      textoVoz={(item) => `${item.articulo} ${item.nombre}`}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
