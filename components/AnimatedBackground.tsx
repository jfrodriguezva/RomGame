"use client";

import { motion } from "framer-motion";
import { useMemo } from "react";
import { useSettings } from "@/lib/settings";

/**
 * Fondo del ambiente. Formas suaves, lentas y muy tenues: acompañan sin robar
 * la atención. Con el "modo calma" activado desaparecen por completo.
 */
const FORMAS = [
  { emoji: "🌿", size: 88 },
  { emoji: "🍃", size: 56 },
  { emoji: "🌾", size: 70 },
  { emoji: "🪴", size: 62 },
  { emoji: "🍂", size: 48 },
  { emoji: "🌼", size: 52 },
];

export default function AnimatedBackground() {
  const calma = useSettings((s) => s.calma);

  const items = useMemo(
    () =>
      FORMAS.map((s, i) => ({
        ...s,
        top: 8 + ((i * 41) % 84),
        left: (i * 61) % 92,
        duration: 22 + (i % 4) * 6,
        delay: i * 1.4,
      })),
    []
  );

  if (calma) return null;

  return (
    <div className="pointer-events-none fixed inset-0 overflow-hidden opacity-[0.13]">
      {items.map((item, i) => (
        <motion.span
          key={i}
          className="absolute select-none"
          style={{ top: `${item.top}%`, left: `${item.left}%`, fontSize: item.size }}
          animate={{ y: [0, -14, 0], rotate: [0, 4, -4, 0] }}
          transition={{
            duration: item.duration,
            delay: item.delay,
            repeat: Infinity,
            ease: "easeInOut",
          }}
        >
          {item.emoji}
        </motion.span>
      ))}
    </div>
  );
}
