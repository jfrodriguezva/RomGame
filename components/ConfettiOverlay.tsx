"use client";

import { motion } from "framer-motion";
import { useMemo } from "react";
import { useSettings } from "@/lib/settings";

/** Papelitos en tonos naturales; en modo calma no aparecen. */
const COLORS = ["#e2b6c1", "#cbd7a7", "#e8cf9a", "#a9c4d6", "#d9c2e0", "#efd9b4"];

export default function ConfettiOverlay({ show }: { show: boolean }) {
  const calma = useSettings((s) => s.calma);

  const pieces = useMemo(
    () =>
      Array.from({ length: 34 }).map((_, i) => ({
        id: i,
        left: Math.random() * 100,
        delay: Math.random() * 0.5,
        duration: 2 + Math.random() * 1.4,
        color: COLORS[i % COLORS.length],
        rotate: Math.random() * 360,
        size: 7 + Math.random() * 6,
      })),
    []
  );

  if (!show || calma) return null;

  return (
    <div className="pointer-events-none fixed inset-0 z-50 overflow-hidden">
      {pieces.map((p) => (
        <motion.div
          key={p.id}
          initial={{ y: -20, opacity: 1, rotate: 0 }}
          animate={{ y: "110vh", opacity: 1, rotate: p.rotate }}
          transition={{ duration: p.duration, delay: p.delay, ease: "easeIn" }}
          style={{
            position: "absolute",
            left: `${p.left}%`,
            top: 0,
            width: p.size,
            height: p.size * 1.4,
            backgroundColor: p.color,
            borderRadius: 3,
          }}
        />
      ))}
    </div>
  );
}
