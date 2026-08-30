"use client";

import { useEffect, useRef, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import { playNote } from "@/lib/audio";
import { hablar, callar } from "@/lib/speech";
import { useProgressStore } from "@/lib/progressStore";

/**
 * El juego del silencio.
 *
 * María Montessori lo descubrió por accidente: una clase entera quedándose
 * quieta para escuchar. No es un premio ni un castigo, es un trabajo más —el
 * de controlar el propio cuerpo— y a los niños les encanta.
 *
 * Sin niveles, sin estrellas y sin final: se queda el tiempo que se quiera.
 */

const FASES = [
  { nombre: "Toma aire", segundos: 4, escala: 1.6 },
  { nombre: "Guárdalo", segundos: 2, escala: 1.6 },
  { nombre: "Suéltalo despacio", segundos: 6, escala: 1 },
  { nombre: "Quieto", segundos: 2, escala: 1 },
];

const DURACIONES = [1, 2, 3, 5];

const SONIDOS_PARA_ESCUCHAR = [
  "¿Escuchas algún pájaro?",
  "¿Se oye la calle a lo lejos?",
  "¿Escuchas tu propia respiración?",
  "¿Hay algún reloj o aparato sonando?",
  "¿Puedes oír el viento?",
];

export default function MesaSilencioPage() {
  const registerPlay = useProgressStore((s) => s.registerPlay);
  const [corriendo, setCorriendo] = useState(false);
  const [fase, setFase] = useState(0);
  const [restante, setRestante] = useState(0);
  const [minutos, setMinutos] = useState(2);
  const [pista, setPista] = useState(SONIDOS_PARA_ESCUCHAR[0]);
  const faseRef = useRef(0);

  useEffect(() => {
    registerPlay("mesa-silencio");
  }, [registerPlay]);

  useEffect(() => {
    if (!corriendo) return;

    const fin = setInterval(() => {
      setRestante((r) => {
        if (r <= 1) {
          clearInterval(fin);
          setCorriendo(false);
          // La campana cierra el silencio, como en el ambiente.
          [0, 4, 7].forEach((n, i) => setTimeout(() => playNote(n, 1.6), i * 260));
          hablar("Muy bien. Puedes moverte otra vez");
          return 0;
        }
        return r - 1;
      });
    }, 1000);

    const respiracion = setInterval(() => {
      faseRef.current = (faseRef.current + 1) % FASES.length;
      setFase(faseRef.current);
      if (faseRef.current === 0) playNote(2, 0.5);
    }, 3500);

    const escuchar = setInterval(() => {
      setPista(SONIDOS_PARA_ESCUCHAR[Math.floor(Math.random() * SONIDOS_PARA_ESCUCHAR.length)]);
    }, 15000);

    return () => {
      clearInterval(fin);
      clearInterval(respiracion);
      clearInterval(escuchar);
    };
  }, [corriendo]);

  function empezar() {
    callar();
    faseRef.current = 0;
    setFase(0);
    setRestante(minutos * 60);
    setCorriendo(true);
    playNote(4, 1.4);
  }

  const actual = FASES[fase];
  const mm = String(Math.floor(restante / 60)).padStart(2, "0");
  const ss = String(restante % 60).padStart(2, "0");

  return (
    <GameShell slug="mesa-silencio" consigna={corriendo ? actual.nombre : "Siéntate cómodo"}>
      <div className="flex flex-col items-center gap-8 pt-4">
        <div className="relative flex h-64 w-64 items-center justify-center">
          <motion.div
            animate={{ scale: corriendo ? actual.escala : 1 }}
            transition={{ duration: corriendo ? actual.segundos * 0.85 : 0.6, ease: "easeInOut" }}
            className="absolute h-32 w-32 rounded-full bg-[#dbe7d6]"
          />
          <motion.div
            animate={{ scale: corriendo ? actual.escala : 1, opacity: corriendo ? 0.5 : 0.3 }}
            transition={{ duration: corriendo ? actual.segundos * 0.85 : 0.6, ease: "easeInOut" }}
            className="absolute h-44 w-44 rounded-full border-2 border-[#a9c0a0]"
          />
          <span className="relative text-5xl">🕯️</span>
        </div>

        {corriendo ? (
          <>
            <span className="font-mono text-3xl font-bold text-[#4a6b4d]">
              {mm}:{ss}
            </span>
            <p className="max-w-xs text-center text-base text-stone-500">{pista}</p>
            <button
              onClick={() => setCorriendo(false)}
              className="rounded-2xl bg-white px-6 py-3 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
            >
              Terminar antes
            </button>
          </>
        ) : (
          <>
            <div className="flex gap-2">
              {DURACIONES.map((m) => (
                <button
                  key={m}
                  onClick={() => setMinutos(m)}
                  className={`h-12 w-14 rounded-2xl text-sm font-extrabold ${
                    minutos === m ? "bg-[#4a6b4d] text-white" : "bg-white text-stone-500"
                  }`}
                >
                  {m} min
                </button>
              ))}
            </div>

            <button
              onClick={empezar}
              className="rounded-3xl bg-[#4a6b4d] px-10 py-5 text-lg font-extrabold text-white shadow active:scale-95"
            >
              Empezar el silencio
            </button>

            <p className="max-w-xs text-center text-sm leading-relaxed text-stone-500">
              Quédate quieto y escucha. Cuando suene la campana, el silencio termina.
            </p>
          </>
        )}
      </div>
    </GameShell>
  );
}
