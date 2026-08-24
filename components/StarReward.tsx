"use client";

import { motion, AnimatePresence } from "framer-motion";

export default function StarReward({
  show,
  message = "¡Muy bien!",
}: {
  show: boolean;
  message?: string;
}) {
  return (
    <AnimatePresence>
      {show && (
        <motion.div
          initial={{ opacity: 0, scale: 0.6 }}
          animate={{ opacity: 1, scale: 1 }}
          exit={{ opacity: 0, scale: 0.6 }}
          className="fixed inset-0 z-40 flex items-center justify-center bg-black/30"
        >
          <div className="flex flex-col items-center gap-3 rounded-3xl bg-white px-10 py-8 shadow-2xl">
            <motion.span
              animate={{ rotate: [0, -10, 10, -10, 0] }}
              transition={{ duration: 0.8, repeat: Infinity }}
              className="text-7xl"
            >
              ⭐
            </motion.span>
            <span className="text-2xl font-extrabold text-amber-500">
              {message}
            </span>
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
