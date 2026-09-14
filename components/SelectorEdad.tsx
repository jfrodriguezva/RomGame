"use client";

import { motion } from "framer-motion";
import Mascot from "./Mascot";
import { useSettings } from "@/lib/settings";

const EDADES = [
  { valor: 2, emoji: "🐣" },
  { valor: 3, emoji: "🌱" },
  { valor: 4, emoji: "🌿" },
  { valor: 5, emoji: "🍀" },
  { valor: 6, emoji: "🌟" },
];

/**
 * Primera pantalla de la app: elegir la edad antes que el tema.
 *
 * Sin esto, un niño de 2-3 años se topa con el mismo catálogo completo que
 * uno de 6 — incluida la lectoescritura, que en el ambiente real recién
 * empieza más adelante. La edad elegida filtra qué materiales aparecen
 * (por su `edad` mínima); se puede cambiar después desde "Mamá y papá".
 */
export default function SelectorEdad() {
  const setEdad = useSettings((s) => s.set);

  return (
    <div className="relative flex min-h-full flex-1 flex-col items-center justify-center px-6 py-10 textura-papel">
      <Mascot size={56} />
      <h1 className="mb-2 mt-4 text-center text-2xl font-extrabold text-stone-700 sm:text-3xl">
        ¿Cuántos años tiene?
      </h1>
      <p className="mb-8 max-w-xs text-center text-sm text-stone-500">
        Así mostramos primero los materiales que le quedan bien. Se puede cambiar cuando quieras
        desde &quot;Mamá y papá&quot;.
      </p>

      <div className="grid grid-cols-3 gap-3 sm:grid-cols-5">
        {EDADES.map((e) => (
          <motion.button
            key={e.valor}
            whileTap={{ scale: 0.92 }}
            onClick={() => setEdad("edad", e.valor)}
            className="flex h-24 w-24 flex-col items-center justify-center gap-1 rounded-[2rem] bg-white shadow-[0_2px_12px_rgba(80,60,40,0.1)] ring-1 ring-black/5 active:scale-95"
          >
            <span className="text-3xl">{e.emoji}</span>
            <span className="text-lg font-extrabold text-stone-700">{e.valor}</span>
            <span className="text-[10px] font-bold text-stone-400">años</span>
          </motion.button>
        ))}
      </div>
    </div>
  );
}
