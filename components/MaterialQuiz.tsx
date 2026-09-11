"use client";

import { type ReactNode, useCallback, useEffect, useMemo, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "./GameShell";
import StarReward from "./StarReward";
import ConfettiOverlay from "./ConfettiOverlay";
import { QUIZ_LEVELS, type QuizLevel } from "@/data/levels/quiz";
import { useMaterial } from "@/lib/useMaterial";
import { consignaTresPeriodos, fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";
import { seededPick, stageOf, STAGE_COUNT } from "@/lib/levels";

export interface ItemQuiz {
  id: string;
  /** Lo que se ve: un emoji, una letra, una figura. */
  visual: string;
  /** Cómo se llama. */
  nombre: string;
  /** Qué dice la voz (por defecto, el nombre). */
  vozTexto?: string;
  /** Agrupa ítems parecidos para la discriminación fina de las últimas etapas. */
  familia?: string;
}

/**
 * Material de nomenclatura con lección de tres periodos.
 *
 * Periodo 1 — el material presenta: "esto es el triángulo", y recién después
 * pregunta. Periodo 2 — "muéstrame el triángulo" entre varias figuras.
 * Periodo 3 — "¿qué es esto?" y el niño elige el nombre. En las últimas
 * etapas el modelo se oculta y hay que recordarlo.
 */
export default function MaterialQuiz({
  slug,
  items,
  pregunta,
  render,
  levels = QUIZ_LEVELS,
}: {
  slug: string;
  items: ItemQuiz[];
  /** Pregunta del periodo 3, p. ej. "¿Qué figura es?". */
  pregunta: string;
  /** Dibujo personalizado del ítem (por defecto se muestra `visual`). */
  render?: (item: ItemQuiz, grande: boolean) => ReactNode;
  levels?: QuizLevel[];
}) {
  const material = useMaterial<QuizLevel>(slug, levels);
  const { config, level, logrado, nota } = material;

  /**
   * Vocabulario disponible en este nivel.
   *
   * Montessori presenta pocos objetos a la vez y va sumando de a poco: en el
   * nivel 1 solo aparecen los primeros del material (los mas basicos) y el
   * repertorio completo llega hasta las ultimas etapas. Por eso el orden del
   * arreglo `items` importa: es el orden de presentacion.
   */
  const disponibles = useMemo(() => {
    const minimo = Math.max(config.opciones + 1, 4);
    const avance = (stageOf(level) - 1) / (STAGE_COUNT - 1);
    const cuantos = Math.round(minimo + (items.length - minimo) * avance);
    return items.slice(0, Math.max(minimo, Math.min(items.length, cuantos)));
  }, [config.opciones, items, level]);

  const [ronda, setRonda] = useState(0);
  const [objetivo, setObjetivo] = useState<ItemQuiz | null>(null);
  const [opciones, setOpciones] = useState<ItemQuiz[]>([]);
  const [presentando, setPresentando] = useState(false);
  const [oculto, setOculto] = useState(false);
  const [elegido, setElegido] = useState<string | null>(null);

  const nuevaRonda = useCallback(() => {
    // En etapas altas las opciones vienen de la misma familia: distinguir un
    // triángulo de otro triángulo es más fino que distinguirlo de un círculo.
    const semilla = Date.now() % 9973;
    let candidatos = disponibles;
    if (config.parecidas) {
      const base = disponibles[Math.floor(Math.random() * disponibles.length)];
      const familia = disponibles.filter((i) => i.familia && i.familia === base.familia);
      if (familia.length >= config.opciones) candidatos = familia;
    }
    const elegidos = seededPick(candidatos, Math.min(config.opciones, candidatos.length), semilla);
    const meta = elegidos[Math.floor(Math.random() * elegidos.length)];

    setOpciones(shuffle(elegidos));
    setObjetivo(meta);
    setElegido(null);
    setOculto(false);

    if (config.periodo === 1) {
      // Presentación: se nombra y se deja ver antes de pedir nada.
      setPresentando(true);
      hablar(`Esto es ${meta.vozTexto ?? meta.nombre}`);
      setTimeout(() => setPresentando(false), 1800);
    } else if (config.periodo === 2) {
      hablar(`Muéstrame ${meta.vozTexto ?? meta.nombre}`);
    } else {
      hablar(pregunta);
      if (config.ocultar) setTimeout(() => setOculto(true), 2600);
    }
  }, [config.opciones, config.parecidas, config.periodo, config.ocultar, disponibles, pregunta]);

  useEffect(() => {
    // Arranca una ronda nueva al cambiar de nivel: nuevaRonda() elige al
    // azar, habla en voz alta y arma temporizadores — no es una derivación
    // pura que se pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setRonda(0);
    nuevaRonda();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function responder(item: ItemQuiz) {
    if (!objetivo || presentando || elegido) return;
    setElegido(item.id);

    if (item.id === objetivo.id) {
      const siguiente = ronda + 1;
      material.acierto();
      hablar(objetivo.vozTexto ?? objetivo.nombre);
      if (siguiente >= config.rondas) {
        setTimeout(() => material.completar(), 700);
      } else {
        setRonda(siguiente);
        setTimeout(nuevaRonda, 900);
      }
    } else {
      // Control del error: la opción vuelve a su sitio y se puede reintentar.
      material.intento();
      setTimeout(() => setElegido(null), 600);
    }
  }

  const consigna = useMemo(() => {
    if (!objetivo) return "";
    if (presentando) return consignaTresPeriodos(1, objetivo.nombre);
    // En el primer periodo el material presenta, no examina: despues de nombrar
    // la figura sigue diciendo cual es. Preguntar "que figura es" aqui seria
    // saltarse dos periodos, y ademas dejaria el nivel imposible si la voz
    // esta apagada o el dispositivo no tiene motor de sintesis.
    if (config.periodo === 1) return `Ahora toca ${objetivo.nombre}`;
    if (config.periodo === 2) return consignaTresPeriodos(2, objetivo.nombre);
    return pregunta;
  }, [config.periodo, objetivo, presentando, pregunta]);

  const mostrarVisual = config.periodo !== 2;
  const dibujar = render ?? ((item: ItemQuiz, grande: boolean) => (
    <span className={grande ? "text-7xl" : "text-4xl"}>{item.visual}</span>
  ));

  return (
    <GameShell
      slug={slug}
      level={level}
      levels={levels.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={consigna}
      nota={nota}
    >
      <ConfettiOverlay show={logrado} slug={slug} />
      <StarReward
        show={logrado}
        slug={slug}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={material.repetir}
      />

      {/* Cuentas de la ronda: el avance se ve, no se cuenta con números. */}
      <div className="mb-4 flex justify-center gap-1.5">
        {Array.from({ length: config.rondas }).map((_, i) => (
          <span
            key={i}
            className={`h-2.5 w-2.5 rounded-full transition ${
              i < ronda ? "bg-amber-400" : "bg-black/10"
            }`}
          />
        ))}
      </div>

      {mostrarVisual && objetivo && (
        <div className="mb-6 flex justify-center">
          <motion.div
            key={objetivo.id + ronda}
            initial={{ scale: 0.85, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="flex h-40 w-40 items-center justify-center rounded-[2rem] bg-white/85 shadow-sm ring-1 ring-black/5"
          >
            <AnimatePresence mode="wait">
              {oculto ? (
                <motion.span
                  key="oculto"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="text-5xl opacity-30"
                >
                  🤍
                </motion.span>
              ) : (
                <motion.div key="visible" exit={{ opacity: 0, scale: 0.9 }}>
                  {dibujar(objetivo, true)}
                </motion.div>
              )}
            </AnimatePresence>
          </motion.div>
        </div>
      )}

      <div
        className={`grid gap-3 ${
          config.opciones <= 4 ? "grid-cols-2" : "grid-cols-3"
        }`}
      >
        {opciones.map((o) => {
          const esFallo = elegido === o.id && o.id !== objetivo?.id;
          const esAcierto = elegido === o.id && o.id === objetivo?.id;
          return (
            <motion.button
              key={o.id}
              onClick={() => responder(o)}
              whileTap={{ scale: 0.94 }}
              animate={esFallo ? { x: [0, -6, 6, -4, 0] } : {}}
              disabled={presentando}
              className={`flex min-h-20 flex-col items-center justify-center gap-1 rounded-3xl bg-white/90 px-3 py-4 text-center shadow-sm ring-1 transition ${
                esAcierto
                  ? "ring-4 ring-emerald-300"
                  : esFallo
                    ? "ring-2 ring-amber-300"
                    : "ring-black/5"
              } ${presentando ? "opacity-50" : ""}`}
            >
              {config.periodo === 2 ? (
                dibujar(o, false)
              ) : (
                <span className="text-base font-extrabold leading-tight text-stone-600">
                  {o.nombre}
                </span>
              )}
            </motion.button>
          );
        })}
      </div>

      {objetivo && config.periodo === 1 && (
        <p className="mt-6 text-center text-sm text-stone-400">
          Escucha y toca lo que acabas de oír.
        </p>
      )}
    </GameShell>
  );
}
