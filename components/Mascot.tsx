"use client";

import { motion } from "framer-motion";

export default function Mascot({ size = 64 }: { size?: number }) {
  return (
    <motion.div
      animate={{ y: [0, -10, 0], rotate: [0, -4, 4, 0] }}
      transition={{ duration: 3, repeat: Infinity, ease: "easeInOut" }}
      style={{ fontSize: size }}
      className="select-none drop-shadow-md"
      aria-hidden
    >
      🧒
    </motion.div>
  );
}
