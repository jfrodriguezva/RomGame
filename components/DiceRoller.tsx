"use client";

import { useState } from "react";
import { motion } from "framer-motion";

const FACES = ["⚀", "⚁", "⚂", "⚃", "⚄", "⚅"];

export default function DiceRoller({ onRoll }: { onRoll: () => void }) {
  const [face, setFace] = useState(0);
  const [rolling, setRolling] = useState(false);

  function roll() {
    if (rolling) return;
    setRolling(true);
    let count = 0;
    const interval = setInterval(() => {
      setFace(Math.floor(Math.random() * 6));
      count++;
      if (count > 10) {
        clearInterval(interval);
        setRolling(false);
        onRoll();
      }
    }, 80);
  }

  return (
    <motion.button
      onClick={roll}
      whileTap={{ scale: 0.9 }}
      animate={rolling ? { rotate: [0, 20, -20, 0] } : {}}
      transition={{ duration: 0.3, repeat: rolling ? Infinity : 0 }}
      className="flex h-32 w-32 items-center justify-center rounded-3xl bg-white text-8xl shadow-xl"
      aria-label="Tirar el dado"
    >
      {FACES[face]}
    </motion.button>
  );
}
