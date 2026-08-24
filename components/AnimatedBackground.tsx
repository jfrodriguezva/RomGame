"use client";

import { motion } from "framer-motion";
import { useMemo } from "react";

const SHAPES = [
  { emoji: "☁️", size: 70 },
  { emoji: "⭐", size: 40 },
  { emoji: "🎈", size: 50 },
  { emoji: "🌸", size: 44 },
  { emoji: "☁️", size: 90 },
  { emoji: "🦋", size: 38 },
  { emoji: "⭐", size: 30 },
  { emoji: "🎈", size: 46 },
];

export default function AnimatedBackground() {
  const items = useMemo(
    () =>
      SHAPES.map((s, i) => ({
        ...s,
        top: 5 + ((i * 37) % 90),
        left: (i * 53) % 95,
        duration: 14 + (i % 5) * 3,
        delay: i * 0.6,
      })),
    []
  );

  return (
    <div className="pointer-events-none fixed inset-0 overflow-hidden opacity-40">
      {items.map((item, i) => (
        <motion.span
          key={i}
          className="absolute select-none"
          style={{ top: `${item.top}%`, left: `${item.left}%`, fontSize: item.size }}
          animate={{ y: [0, -18, 0], x: [0, 10, 0], rotate: [0, 6, -6, 0] }}
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
