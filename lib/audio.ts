const effects = {
  correct: "/audio/effects/correct.mp3",
  wrong: "/audio/effects/wrong.mp3",
  win: "/audio/effects/win.mp3",
  click: "/audio/effects/click.mp3",
  star: "/audio/effects/star.mp3",
} as const;

export type SoundEffect = keyof typeof effects;

export function playSound(name: SoundEffect) {
  if (typeof window === "undefined") return;
  const src = effects[name];
  const audio = new Audio(src);
  audio.volume = 0.7;
  audio.play().catch(() => {
    // El archivo aún no existe o el navegador bloqueó el autoplay; se ignora.
  });
}
