"use client";

import Link from "next/link";

export default function BackHomeButton() {
  return (
    <Link
      href="/"
      className="fixed left-4 top-4 z-30 flex h-12 w-12 items-center justify-center rounded-full bg-white text-2xl shadow-lg active:scale-90"
      aria-label="Volver al inicio"
    >
      🏠
    </Link>
  );
}
