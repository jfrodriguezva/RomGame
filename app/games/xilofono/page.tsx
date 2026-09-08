"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import { BARRAS_XILOFONO } from "@/data/levels/xilofono";
import { playNote } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function XilofonoPage() {
  const registerPlay = useProgressStore((s) => s.registerPlay);
  const [activa, setActiva] = useState<number | null>(null);

  useEffect(() => {
    registerPlay("xilofono");
  }, [registerPlay]);

  function tocar(nota: number) {
    playNote(nota, 0.5);
    setActiva(nota);
    setTimeout(() => setActiva((n) => (n === nota ? null : n)), 220);
  }

  return (
    <div className="min-h-full flex-1 bg-[#f6f0e6] pb-10">
      <BackHomeButton />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-[#7a6234] sm:text-3xl">
          🎼 Xilófono
        </h1>
        <p className="mb-8 text-center text-sm text-[#9c8a63]">
          Toca las barras. Sin niveles, sin puntaje: solo para escuchar cómo suena.
        </p>

        <div className="flex w-full items-end justify-center gap-2">
          {BARRAS_XILOFONO.map((b, i) => (
            <motion.button
              key={b.nota}
              onClick={() => tocar(b.nota)}
              whileTap={{ scale: 0.94 }}
              animate={activa === b.nota ? { y: -8 } : { y: 0 }}
              aria-label={`Nota ${i + 1}, color de ${b.etiqueta}`}
              className="rounded-2xl shadow-sm ring-1 ring-black/10"
              style={{
                backgroundColor: b.color,
                width: 36,
                height: 130 - i * 8,
              }}
            />
          ))}
        </div>
      </main>
    </div>
  );
}
