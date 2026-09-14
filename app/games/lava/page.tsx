"use client";

import { useEffect, useRef, useState } from "react";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import {
  LAVA_LEVELS,
  TICK_MS,
  PLAYER_X,
  PLAYER_WIDTH,
  OBSTACLE_WIDTH,
  JUMP_MS,
  JUMP_HEIGHT,
  CLEAR_HEIGHT,
  START_LIVES,
} from "@/data/levels/lava";
import { playSound } from "@/lib/audio";
import { vibrar, HAPTIC } from "@/lib/haptics";
import { useProgressStore } from "@/lib/progressStore";

interface Llama {
  id: number;
  x: number;
  pasada: boolean;
}

let nextId = 0;
const PISTA_ANCHO = 420;

/**
 * "El piso es lava", como el dinosaurio de Chrome: el conejo corre solo y
 * las llamaradas se acercan con física real y continua. El salto se
 * comprueba en cada cuadro comparando posiciones de verdad (no un cálculo
 * de "en qué baldosa vas a caer" a un tiempo fijo) — así funciona igual de
 * bien despacio que rápido, y saltar en el momento correcto siempre libra
 * el obstáculo que tienes encima.
 */
export default function LavaPage() {
  const [level, setLevel] = useState(1);
  const config = LAVA_LEVELS.find((l) => l.level === level)!;
  const [llamas, setLlamas] = useState<Llama[]>([]);
  const [alturaSalto, setAlturaSalto] = useState(0);
  const [golpeado, setGolpeado] = useState(false);
  const [lives, setLives] = useState(START_LIVES);
  const [cruzadas, setCruzadas] = useState(0);
  const [milestone, setMilestone] = useState(false);

  const llamasRef = useRef<Llama[]>([]);
  const saltandoRef = useRef(false);
  const jumpStartRef = useRef(0);
  const invulnerableHastaRef = useRef(0);
  const msDesdeSpawnRef = useRef(0);
  const pausadoRef = useRef(false);
  const livesRef = useRef(START_LIVES);
  const cruzadasRef = useRef(0);

  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  function resetRun() {
    setLlamas([]);
    llamasRef.current = [];
    saltandoRef.current = false;
    setAlturaSalto(0);
    setLives(START_LIVES);
    livesRef.current = START_LIVES;
    setCruzadas(0);
    cruzadasRef.current = 0;
    pausadoRef.current = false;
    msDesdeSpawnRef.current = 0;
    invulnerableHastaRef.current = 0;
  }

  useEffect(() => {
    registerPlay("lava");
    // Reinicia la carrera al cambiar de nivel: sincroniza con una prop que
    // cambia, no es una derivación pura del render actual.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    resetRun();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  useEffect(() => {
    const tick = setInterval(() => {
      if (pausadoRef.current) return;
      const ahora = Date.now();

      // 1. Salto: altura real en este instante, no un valor fijo al tocar.
      let altura = 0;
      if (saltandoRef.current) {
        const t = Math.min(1, (ahora - jumpStartRef.current) / JUMP_MS);
        altura = JUMP_HEIGHT * 4 * t * (1 - t);
        if (t >= 1) {
          saltandoRef.current = false;
        }
      }
      setAlturaSalto(altura);

      // 2. Mueve las llamas y arma spawns nuevos.
      msDesdeSpawnRef.current += TICK_MS;
      let siguientes = llamasRef.current
        .map((l) => ({ ...l, x: l.x - config.speed }))
        .filter((l) => l.x > -OBSTACLE_WIDTH);

      if (msDesdeSpawnRef.current >= config.obstacleEveryMs) {
        msDesdeSpawnRef.current = 0;
        siguientes = [...siguientes, { id: nextId++, x: PISTA_ANCHO, pasada: false }];
      }

      // 3. Colisión: ¿alguna llama se solapa con el conejo y el salto no
      // alcanza a librarla? Se revisa cada cuadro con la posición real.
      let golpe = false;
      if (ahora >= invulnerableHastaRef.current) {
        for (const l of siguientes) {
          const solapa = l.x < PLAYER_X + PLAYER_WIDTH && l.x + OBSTACLE_WIDTH > PLAYER_X;
          if (solapa && altura < CLEAR_HEIGHT) {
            golpe = true;
            siguientes = siguientes.filter((o) => o.id !== l.id);
            break;
          }
        }
      }

      // 4. Cuenta las llamas que quedaron atrás sin golpear: esa es la
      // puntuación real de cuántos obstáculos se libraron de verdad.
      let nuevasCruzadas = 0;
      siguientes = siguientes.map((l) => {
        if (!l.pasada && l.x + OBSTACLE_WIDTH < PLAYER_X) {
          nuevasCruzadas++;
          return { ...l, pasada: true };
        }
        return l;
      });

      llamasRef.current = siguientes;
      setLlamas(siguientes);

      if (nuevasCruzadas > 0) {
        const anterior = cruzadasRef.current;
        const total = anterior + nuevasCruzadas;
        cruzadasRef.current = total;
        setCruzadas(total);
        if (Math.floor(total / 20) > Math.floor(anterior / 20)) {
          addStars("lava", 1);
          playSound("win");
          setMilestone(true);
          setTimeout(() => setMilestone(false), 1200);
        }
      }

      if (golpe) {
        invulnerableHastaRef.current = ahora + 900;
        playSound("wrong");
        vibrar(HAPTIC.error);
        setGolpeado(true);
        setTimeout(() => setGolpeado(false), 350);
        const next = Math.max(0, livesRef.current - 1);
        livesRef.current = next;
        setLives(next);
        if (next <= 0) {
          pausadoRef.current = true;
          setTimeout(resetRun, 900);
        }
      }
    }, TICK_MS);
    return () => clearInterval(tick);
    // Solo debe reiniciar el intervalo cuando cambia la velocidad o el
    // ritmo de aparición (nivel nuevo); addStars es un setter estable de
    // zustand y no necesita estar en las dependencias.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [config.speed, config.obstacleEveryMs]);

  function saltar() {
    if (saltandoRef.current || pausadoRef.current) return;
    saltandoRef.current = true;
    jumpStartRef.current = Date.now();
    playSound("click");
  }

  return (
    <div
      className="relative min-h-full flex-1 touch-none select-none overflow-hidden bg-gradient-to-b from-orange-200 via-amber-100 to-white pb-10"
      onPointerDown={saltar}
    >
      <BackHomeButton />
      <ConfettiOverlay show={milestone} slug="lava" />
      <main className="pointer-events-none mx-auto w-full max-w-xl px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-orange-700 sm:text-3xl">
          🌋 El piso es lava
        </h1>
        <p className="mb-3 text-center text-slate-600">
          Toca la pantalla para saltar las llamaradas
        </p>
        <div className="mb-3 flex items-center justify-center gap-4">
          <span className="text-lg">
            {"❤️".repeat(lives)}
            {"🖤".repeat(Math.max(0, START_LIVES - lives))}
          </span>
          <span className="rounded-full bg-white px-3 py-1 text-sm font-bold text-orange-600 shadow">
            Llamaradas libradas: {cruzadas}
          </span>
        </div>
        <div className="pointer-events-auto mb-4 flex justify-center">
          <LevelSelector
            gameId="lava"
            levels={LAVA_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>
      </main>

      <div
        className="relative mx-auto h-40 max-w-2xl overflow-hidden"
        style={{ maxWidth: PISTA_ANCHO }}
      >
        <div className="absolute bottom-0 h-3 w-full rounded-full bg-gradient-to-b from-stone-400 to-stone-500" />

        {llamas.map((l) => (
          <span
            key={l.id}
            className="absolute bottom-3 text-3xl"
            style={{ left: l.x, width: OBSTACLE_WIDTH }}
          >
            🔥
          </span>
        ))}

        <span
          className={`absolute bottom-3 text-4xl transition-transform ${
            golpeado ? "animate-pulse" : ""
          }`}
          style={{ left: PLAYER_X, transform: `translateY(${-alturaSalto}px)` }}
        >
          🐰
        </span>
      </div>
    </div>
  );
}
