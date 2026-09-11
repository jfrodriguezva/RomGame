"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  DIETA_ANIMAL_LEVELS,
  ANIMALES_DIETA,
  type AnimalDieta,
  type DietaAnimalLevel,
} from "@/data/levels/dieta-animal";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "herbivoro", nombre: "Come plantas", emoji: "🌿" },
  { clave: "carnivoro", nombre: "Come carne", emoji: "🍖" },
  { clave: "omnivoro", nombre: "Come de todo", emoji: "🍽️" },
];

export default function DietaAnimalPage() {
  return (
    <MaterialClasificar<DietaAnimalLevel, AnimalDieta>
      slug="dieta-animal"
      levels={DIETA_ANIMAL_LEVELS}
      consigna={() => "¿Qué come este animal?"}
      disponibles={() => ANIMALES_DIETA}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.dieta}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) =>
        `${item.nombre} ${item.dieta === "herbivoro" ? "come plantas" : item.dieta === "carnivoro" ? "come carne" : "come de todo"}`
      }
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
