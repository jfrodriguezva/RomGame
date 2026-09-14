"use client";

import { useEffect, useRef, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { MEMORIA_TURNOS_LEVELS } from "@/data/levels/memoria-turnos";
import { MEMORAMA_FACES } from "@/data/assets";
import { pickRandom, shuffle } from "@/lib/shuffle";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { playSound } from "@/lib/audio";
import { vibrar, HAPTIC } from "@/lib/haptics";

interface Card {
  id: number;
  face: string;
}

function buildDeck(pairs: number): Card[] {
  const faces = pickRandom(MEMORAMA_FACES, pairs);
  return shuffle([...faces, ...faces]).map((face, id) => ({ id, face }));
}

/**
 * Memoria por turnos contra la computadora — distinto del memorama solo:
 * aquí importa de quién es el turno y quién lleva más parejas, no solo
 * encontrarlas todas. La computadora "recuerda" lo que ya se destapó
 * (incluidas las cartas que voltea el jugador) y usa esa memoria si le
 * conviene, en vez de jugar a ciegas todo el tiempo.
 */
export default function MemoriaTurnosPage() {
  const material = useMaterial<(typeof MEMORIA_TURNOS_LEVELS)[number]>(
    "memoria-turnos",
    MEMORIA_TURNOS_LEVELS
  );
  const { config, level, logrado, nota } = material;

  const [deck, setDeck] = useState<Card[]>([]);
  const [flipped, setFlipped] = useState<number[]>([]);
  const [matched, setMatched] = useState<number[]>([]);
  const [turno, setTurno] = useState<"jugador" | "cpu">("jugador");
  const [puntos, setPuntos] = useState({ jugador: 0, cpu: 0 });
  const [bloqueado, setBloqueado] = useState(false);
  const [resultado, setResultado] = useState<string | null>(null);

  const memoriaCpu = useRef<Map<number, string>>(new Map());
  const deckRef = useRef<Card[]>([]);
  const matchedRef = useRef<number[]>([]);

  useEffect(() => {
    const nuevoDeck = buildDeck(config.pairs);
    // Arma y baraja el mazo del nivel: no es una derivación pura que se
    // pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setDeck(nuevoDeck);
    deckRef.current = nuevoDeck;
    setFlipped([]);
    setMatched([]);
    matchedRef.current = [];
    setTurno("jugador");
    setPuntos({ jugador: 0, cpu: 0 });
    setBloqueado(false);
    setResultado(null);
    memoriaCpu.current = new Map();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function resolverPar(a: number, b: number, quien: "jugador" | "cpu") {
    const cardA = deckRef.current.find((c) => c.id === a)!;
    const cardB = deckRef.current.find((c) => c.id === b)!;
    memoriaCpu.current.set(a, cardA.face);
    memoriaCpu.current.set(b, cardB.face);

    setTimeout(() => {
      if (cardA.face === cardB.face) {
        playSound("correct");
        vibrar(HAPTIC.acierto);
        const siguienteMatched = [...matchedRef.current, a, b];
        matchedRef.current = siguienteMatched;
        setMatched(siguienteMatched);
        setFlipped([]);
        setPuntos((p) => ({ ...p, [quien]: p[quien] + 1 }));

        if (siguienteMatched.length === deckRef.current.length) {
          setBloqueado(true);
          setTimeout(() => {
            setPuntos((p) => {
              const texto =
                p.jugador > p.cpu
                  ? "¡Ganaste tú!"
                  : p.jugador < p.cpu
                    ? "Ganó la computadora, ¡otra vez!"
                    : "¡Empate!";
              setResultado(texto);
              return p;
            });
            material.completar();
          }, 400);
        } else {
          setBloqueado(false);
        }
      } else {
        playSound("wrong");
        setFlipped([]);
        setTurno(quien === "jugador" ? "cpu" : "jugador");
        setBloqueado(false);
      }
    }, 700);
  }

  function tocar(id: number) {
    if (turno !== "jugador" || bloqueado) return;
    if (flipped.includes(id) || matchedRef.current.includes(id)) return;

    const siguiente = [...flipped, id];
    setFlipped(siguiente);
    playSound("click");

    if (siguiente.length === 2) {
      setBloqueado(true);
      resolverPar(siguiente[0], siguiente[1], "jugador");
    }
  }

  // Turno de la computadora: usa su memoria si ya vio una pareja, si no
  // elige al azar entre las cartas que aún no ha visto.
  useEffect(() => {
    if (turno !== "cpu" || bloqueado || logrado) return;
    const t = setTimeout(() => {
      const disponibles = deckRef.current.filter((c) => !matchedRef.current.includes(c.id));
      const porCara = new Map<string, number[]>();
      memoriaCpu.current.forEach((face, id) => {
        if (matchedRef.current.includes(id)) return;
        porCara.set(face, [...(porCara.get(face) ?? []), id]);
      });
      const parConocido = [...porCara.values()].find((ids) => ids.length >= 2);

      let elegidas: number[];
      if (parConocido) {
        elegidas = parConocido.slice(0, 2);
      } else {
        const sinVer = disponibles.filter((c) => !memoriaCpu.current.has(c.id));
        const pool = sinVer.length >= 2 ? sinVer : disponibles;
        elegidas = shuffle(pool)
          .slice(0, 2)
          .map((c) => c.id);
      }

      setFlipped(elegidas);
      setBloqueado(true);
      resolverPar(elegidas[0], elegidas[1], "cpu");
    }, 900);
    return () => clearTimeout(t);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [turno, bloqueado, logrado]);

  const cols = config.pairs <= 4 ? "grid-cols-4" : config.pairs <= 6 ? "grid-cols-4" : "grid-cols-5";

  return (
    <GameShell
      slug="memoria-turnos"
      level={level}
      levels={MEMORIA_TURNOS_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={resultado ?? (turno === "jugador" ? "Tu turno: toca dos cartas" : "Turno de la computadora")}
      nota={nota}
    >
      <ConfettiOverlay show={logrado} slug="memoria-turnos" />
      <StarReward
        slug="memoria-turnos"
        show={logrado}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={material.repetir}
      />

      <div className="mb-4 flex items-center justify-center gap-6 text-sm font-bold">
        <span className={turno === "jugador" ? "text-rose-500" : "text-stone-400"}>
          🧑 Tú: {puntos.jugador}
        </span>
        <span className={turno === "cpu" ? "text-rose-500" : "text-stone-400"}>
          🤖 CPU: {puntos.cpu}
        </span>
      </div>

      <div className={`grid ${cols} gap-2.5`}>
        {deck.map((card) => {
          const isFlipped = flipped.includes(card.id) || matched.includes(card.id);
          return (
            <button
              key={card.id}
              onClick={() => tocar(card.id)}
              className="aspect-square"
              style={{ perspective: 600 }}
              aria-label="carta"
            >
              <motion.div
                animate={{ rotateY: isFlipped ? 180 : 0 }}
                transition={{ duration: 0.35 }}
                className="relative h-full w-full"
                style={{ transformStyle: "preserve-3d" }}
              >
                <div
                  className="absolute inset-0 flex items-center justify-center rounded-2xl bg-gradient-to-br from-rose-400 to-pink-500 text-2xl shadow-md"
                  style={{ backfaceVisibility: "hidden" }}
                >
                  ❓
                </div>
                <div
                  className="absolute inset-0 flex items-center justify-center rounded-2xl bg-white text-3xl shadow-md"
                  style={{ backfaceVisibility: "hidden", transform: "rotateY(180deg)" }}
                >
                  {card.face}
                </div>
              </motion.div>
            </button>
          );
        })}
      </div>
    </GameShell>
  );
}
