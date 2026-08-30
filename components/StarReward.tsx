"use client";

import { motion, AnimatePresence } from "framer-motion";

/**
 * Cierre de nivel.
 *
 * No dice "ganaste" ni muestra un puntaje contra nadie: reconoce el trabajo
 * terminado y ofrece dos caminos igual de válidos, repetir o continuar.
 * En Montessori repetir un material no es retroceder, es lo normal.
 */
export default function StarReward({
  show,
  message = "Lo lograste",
  stars = 0,
  level,
  onNext,
  onRepeat,
  nextLabel = "Siguiente",
}: {
  show: boolean;
  message?: string;
  stars?: number;
  level?: number;
  onNext?: () => void;
  onRepeat?: () => void;
  nextLabel?: string;
}) {
  return (
    <AnimatePresence>
      {show && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 z-40 flex items-center justify-center bg-stone-900/25 p-6 backdrop-blur-[2px]"
        >
          <motion.div
            initial={{ scale: 0.9, y: 12 }}
            animate={{ scale: 1, y: 0 }}
            exit={{ scale: 0.9, opacity: 0 }}
            transition={{ type: "spring", stiffness: 260, damping: 22 }}
            className="flex w-full max-w-xs flex-col items-center gap-3 rounded-[2rem] bg-[#fdfaf5] px-7 py-8 text-center shadow-2xl"
          >
            <motion.span
              animate={{ rotate: [0, -8, 8, 0] }}
              transition={{ duration: 1.6, repeat: Infinity, ease: "easeInOut" }}
              className="text-6xl"
            >
              🌟
            </motion.span>

            <span className="text-xl font-extrabold text-stone-700">{message}</span>

            {level !== undefined && (
              <span className="-mt-2 text-sm text-stone-400">Nivel {level}</span>
            )}

            {stars > 0 && (
              <div className="flex gap-1 text-2xl text-amber-400">
                {Array.from({ length: stars }).map((_, i) => (
                  <motion.span
                    key={i}
                    initial={{ scale: 0, rotate: -40 }}
                    animate={{ scale: 1, rotate: 0 }}
                    transition={{ delay: 0.15 + i * 0.12, type: "spring", stiffness: 300 }}
                  >
                    ★
                  </motion.span>
                ))}
              </div>
            )}

            {(onNext || onRepeat) && (
              <div className="mt-2 flex w-full flex-col gap-2">
                {onNext && (
                  <button
                    onClick={onNext}
                    className="w-full rounded-2xl bg-stone-700 py-3 text-base font-extrabold text-white shadow active:scale-95"
                  >
                    {nextLabel}
                  </button>
                )}
                {onRepeat && (
                  <button
                    onClick={onRepeat}
                    className="w-full rounded-2xl bg-white py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
                  >
                    Hacerlo otra vez
                  </button>
                )}
              </div>
            )}
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
