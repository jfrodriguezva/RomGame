"use client";

import { motion } from "framer-motion";
import { useSettings } from "@/lib/settings";

export default function Mascot({ size = 64 }: { size?: number }) {
  const calma = useSettings((s) => s.calma);
  return (
    <motion.div
      animate={calma ? undefined : { y: [0, -8, 0] }}
      transition={{ duration: 4, repeat: Infinity, ease: "easeInOut" }}
      style={{ fontSize: size }}
      className="select-none"
      aria-hidden
    >
      🦉
    </motion.div>
  );
}
