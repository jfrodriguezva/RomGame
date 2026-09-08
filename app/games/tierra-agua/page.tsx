"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { FORMAS_TIERRA_AGUA } from "@/data/levels/tierra-agua";

const AGUA = "#a9d1de";
const TIERRA = "#c9a06a";
const TRAZO = "#6b5233";

const ITEMS: ItemQuiz[] = FORMAS_TIERRA_AGUA.map((f) => ({
  id: f.id,
  visual: f.id,
  nombre: f.label,
  familia: "tierra-agua",
}));

/** Diagrama plano de dos colores: la relación importa, no el realismo. */
function Icono({ id, size }: { id: string; size: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
      <rect x={4} y={4} width={92} height={92} rx={14} fill={AGUA} stroke={TRAZO} strokeWidth={3} />

      {id === "isla" && (
        <circle cx={50} cy={50} r={28} fill={TIERRA} stroke={TRAZO} strokeWidth={2.5} />
      )}

      {id === "lago" && (
        <>
          <rect x={4} y={4} width={92} height={92} rx={14} fill={TIERRA} stroke={TRAZO} strokeWidth={3} />
          <circle cx={50} cy={50} r={24} fill={AGUA} stroke={TRAZO} strokeWidth={2.5} />
        </>
      )}

      {id === "peninsula" && (
        <path
          d="M4 18 H50 C68 18 78 34 78 50 C78 66 68 82 50 82 H4 Z"
          fill={TIERRA}
          stroke={TRAZO}
          strokeWidth={2.5}
        />
      )}

      {id === "cabo" && (
        <path d="M4 40 H40 L82 50 L40 60 H4 Z" fill={TIERRA} stroke={TRAZO} strokeWidth={2.5} />
      )}

      {id === "golfo" && (
        <>
          <rect x={4} y={4} width={92} height={92} rx={14} fill={TIERRA} stroke={TRAZO} strokeWidth={3} />
          <path d="M18 4 C18 32 82 32 82 4 Z" fill={AGUA} stroke={TRAZO} strokeWidth={2.5} />
        </>
      )}

      {id === "estrecho" && (
        <>
          <path d="M4 4 H38 V96 H4 Z" fill={TIERRA} stroke={TRAZO} strokeWidth={2.5} />
          <path d="M62 4 H96 V96 H62 Z" fill={TIERRA} stroke={TRAZO} strokeWidth={2.5} />
        </>
      )}

      {id === "istmo" && (
        <>
          <path d="M4 4 H96 V28 H4 Z" fill={TIERRA} stroke={TRAZO} strokeWidth={2.5} />
          <path d="M4 72 H96 V96 H4 Z" fill={TIERRA} stroke={TRAZO} strokeWidth={2.5} />
          <path d="M38 4 H62 V96 H38 Z" fill={TIERRA} stroke={TRAZO} strokeWidth={2.5} />
        </>
      )}

      {id === "archipielago" && (
        <>
          <circle cx={30} cy={35} r={12} fill={TIERRA} stroke={TRAZO} strokeWidth={2} />
          <circle cx={67} cy={28} r={9} fill={TIERRA} stroke={TRAZO} strokeWidth={2} />
          <circle cx={50} cy={65} r={14} fill={TIERRA} stroke={TRAZO} strokeWidth={2} />
          <circle cx={78} cy={70} r={7} fill={TIERRA} stroke={TRAZO} strokeWidth={2} />
        </>
      )}
    </svg>
  );
}

export default function TierraAguaPage() {
  return (
    <MaterialQuiz
      slug="tierra-agua"
      items={ITEMS}
      pregunta="¿Qué forma es?"
      render={(item, grande) => <Icono id={item.id} size={grande ? 118 : 58} />}
    />
  );
}
