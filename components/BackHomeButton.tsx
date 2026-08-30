"use client";

import Link from "next/link";
import { playSound } from "@/lib/audio";

export default function BackHomeButton({ href = "/" }: { href?: string }) {
  return (
    <Link
      href={href}
      onClick={() => playSound("click")}
      className="fixed left-3 top-3 z-30 flex h-11 w-11 items-center justify-center rounded-2xl bg-white/90 text-xl shadow-sm ring-1 ring-black/5 backdrop-blur active:scale-90"
      aria-label="Volver al inicio"
    >
      ←
    </Link>
  );
}
