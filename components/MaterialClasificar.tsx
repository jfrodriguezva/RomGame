"use client";

import { type ReactNode, useCallback, useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "./GameShell";
import StarReward from "./StarReward";
import ConfettiOverlay from "./ConfettiOverlay";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

export interface Canasta {
  clave: string;
  nombre: string;
  emoji?: string;
}

export interface ColorCanasta {
  fondo: string;
  texto: string;
  chip: string;
}

const COLOR_POR_DEFECTO: ColorCanasta = {
  fondo: "bg-[#f1e4d0]",
  texto: "text-[#6b5233]",
  chip: "text-[#8a6a44]",
};

/**
 * Clasificación en canastas: se muestra un elemento pendiente a la vez y N
 * canastas donde soltarlo. Control del error: si no toca esa canasta, no se
 * mueve nada y se invita a intentar otra vez — igual que en el ambiente
 * real, donde la pieza simplemente no encaja en el lugar equivocado.
 *
 * Patrón compartido por ¿Vivo o no vivo?, Cuenta las sílabas, El o la y
 * Pares e impares. Antes de extraerlo cada uno tenía su propia copia de
 * esta página — ver la nota en docs/MANUAL-TECNICO.md, sección 4.
 */
export default function MaterialClasificar<Level extends { level: number }, Item>({
  slug,
  levels,
  consigna,
  disponibles,
  cantidadPorRonda,
  canastas,
  claveDe,
  keyDe,
  render,
  renderChip,
  mensajeError,
  textoVoz,
  colorCanasta = COLOR_POR_DEFECTO,
  tamanoCaja = "h-40 w-40",
  onEscuchar,
}: {
  slug: string;
  levels: Level[];
  /** Consigna que se muestra en el encabezado; puede depender del nivel. */
  consigna: (config: Level) => string;
  /** Elementos disponibles para clasificar en este nivel. */
  disponibles: (config: Level) => Item[];
  /** Cuántos elementos hay que clasificar en la ronda. */
  cantidadPorRonda: (config: Level) => number;
  /** Las canastas activas en este nivel. */
  canastas: (config: Level) => Canasta[];
  /** A qué canasta pertenece un elemento (el nivel importa: en materiales
   * como ¿Vivo o no vivo? el criterio de clasificación cambia con la
   * etapa). */
  claveDe: (item: Item, config: Level) => string;
  /** Identificador único del elemento, para las keys de React. */
  keyDe: (item: Item) => string;
  /** Cómo se ve el elemento pendiente, grande, al centro. */
  render: (item: Item) => ReactNode;
  /** Cómo se ve el elemento ya clasificado, chico, dentro de la canasta. */
  renderChip: (item: Item) => ReactNode;
  /** Mensaje del control del error al soltar en la canasta equivocada. */
  mensajeError?: (item: Item) => string;
  /** Qué se dice en voz alta al acertar. Si no se da, no habla nada. */
  textoVoz?: (item: Item) => string;
  /** Color de las canastas; por defecto el tono lino del resto de la app. */
  colorCanasta?: ColorCanasta;
  /** Tamaño de la caja del elemento pendiente. */
  tamanoCaja?: string;
  /** Si se da, la caja del elemento pendiente se puede tocar para oírlo. */
  onEscuchar?: (item: Item) => void;
}) {
  const material = useMaterial<Level>(slug, levels);
  const { config, level, logrado, nota } = material;

  const [pendientes, setPendientes] = useState<Item[]>([]);
  const [conteo, setConteo] = useState<Record<string, Item[]>>({});

  const cestas = canastas(config);

  const preparar = useCallback(() => {
    const pool = disponibles(config);
    const n = cantidadPorRonda(config);
    setPendientes(shuffle(pool).slice(0, Math.min(n, pool.length)));
    setConteo(Object.fromEntries(canastas(config).map((c) => [c.clave, []])));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [config]);

  useEffect(() => {
    // preparar() no es una derivación pura: baraja al azar y depende de
    // config, que cambia con el nivel — no se puede calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    preparar();
  }, [preparar]);

  const actual = pendientes[0];

  function elegir(clave: string) {
    if (!actual) return;
    if (claveDe(actual, config) !== clave) {
      material.intento(mensajeError?.(actual));
      return;
    }
    const restantes = pendientes.slice(1);
    setPendientes(restantes);
    setConteo((c) => ({ ...c, [clave]: [...(c[clave] ?? []), actual] }));
    if (textoVoz) hablar(textoVoz(actual));
    if (restantes.length === 0) setTimeout(() => material.completar(), 450);
    else material.acierto();
  }

  const columnas = cestas.length === 3 ? "grid-cols-3" : "grid-cols-2";

  const caja = (
    <div
      className={`flex ${tamanoCaja} items-center justify-center rounded-[2rem] bg-white/85 shadow-sm ring-1 ring-black/5`}
    >
      <AnimatePresence mode="wait">
        {actual && (
          <motion.div
            key={keyDe(actual)}
            initial={{ scale: 0.7, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.6, opacity: 0 }}
          >
            {render(actual)}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );

  return (
    <GameShell
      slug={slug}
      level={level}
      levels={levels.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={consigna(config)}
      nota={nota}
      acciones={
        <button
          onClick={preparar}
          className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
        >
          Volver a empezar
        </button>
      }
    >
      <ConfettiOverlay show={logrado} slug={slug} />
      <StarReward
        show={logrado}
        slug={slug}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={() => {
          preparar();
          material.repetir();
        }}
      />

      <div className="mb-6 flex flex-col items-center">
        <span className="mb-2 text-xs font-bold uppercase tracking-wide text-stone-400">
          quedan {pendientes.length}
        </span>
        {onEscuchar ? (
          <button
            type="button"
            onClick={() => actual && onEscuchar(actual)}
            aria-label="Escuchar"
          >
            {caja}
          </button>
        ) : (
          caja
        )}
      </div>

      <div className={`grid gap-3 ${columnas}`}>
        {cestas.map((c) => (
          <motion.button
            key={c.clave}
            whileTap={{ scale: 0.95 }}
            onClick={() => elegir(c.clave)}
            className={`flex min-h-32 flex-col items-center gap-1 rounded-[2rem] p-3 ring-1 ring-black/5 ${colorCanasta.fondo}`}
          >
            {c.emoji ? (
              <>
                <span className="text-2xl">{c.emoji}</span>
                <span className={`text-xs font-extrabold ${colorCanasta.texto}`}>{c.nombre}</span>
              </>
            ) : (
              <span className={`text-xl font-extrabold ${colorCanasta.texto}`}>{c.nombre}</span>
            )}
            <span className={`mt-1 flex flex-wrap justify-center gap-1 text-lg ${colorCanasta.chip}`}>
              {conteo[c.clave]?.map((item) => (
                <motion.span key={keyDe(item)} initial={{ scale: 0 }} animate={{ scale: 1 }}>
                  {renderChip(item)}
                </motion.span>
              ))}
            </span>
          </motion.button>
        ))}
      </div>
    </GameShell>
  );
}
