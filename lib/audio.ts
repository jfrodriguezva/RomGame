/**
 * Audio del juego.
 *
 * Los sonidos se sintetizan con WebAudio en vez de cargar archivos: así la app
 * suena desde el primer momento, pesa menos y funciona sin conexión dentro del
 * APK. Si algún día pones tus propios .mp3 en /public/audio/effects/, activa
 * `usarArchivos()` y se usarán esos.
 */

import { getSettings, subscribeSettings } from "./settings";

const FILES = {
  correct: "/audio/effects/correct.mp3",
  wrong: "/audio/effects/wrong.mp3",
  win: "/audio/effects/win.mp3",
  click: "/audio/effects/click.mp3",
  star: "/audio/effects/star.mp3",
} as const;

export type SoundEffect = keyof typeof FILES;

let preferFiles = false;
export function usarArchivos(value = true) {
  preferFiles = value;
}

let ctx: AudioContext | null = null;

function audioCtx(): AudioContext | null {
  if (typeof window === "undefined") return null;
  if (!ctx) {
    const Ctor =
      window.AudioContext ??
      (window as unknown as { webkitAudioContext?: typeof AudioContext }).webkitAudioContext;
    if (!Ctor) return null;
    ctx = new Ctor();
  }
  if (ctx.state === "suspended") void ctx.resume();
  return ctx;
}

interface ToneOptions {
  freq: number;
  duration: number;
  delay?: number;
  type?: OscillatorType;
  volume?: number;
  slideTo?: number;
}

function tone({ freq, duration, delay = 0, type = "sine", volume = 0.18, slideTo }: ToneOptions) {
  const ac = audioCtx();
  if (!ac) return;
  const start = ac.currentTime + delay;
  const osc = ac.createOscillator();
  const gain = ac.createGain();
  osc.type = type;
  osc.frequency.setValueAtTime(freq, start);
  if (slideTo) osc.frequency.exponentialRampToValueAtTime(slideTo, start + duration);
  // Envolvente suave: nada de clics secos, el oído infantil agradece el ataque lento.
  gain.gain.setValueAtTime(0.0001, start);
  gain.gain.exponentialRampToValueAtTime(volume, start + 0.02);
  gain.gain.exponentialRampToValueAtTime(0.0001, start + duration);
  osc.connect(gain).connect(ac.destination);
  osc.start(start);
  osc.stop(start + duration + 0.05);
}

/** Notas de la escala pentatónica de do: cualquier combinación suena bien. */
const PENTA = [523.25, 587.33, 659.25, 783.99, 880.0, 1046.5];

const SYNTH: Record<SoundEffect, () => void> = {
  click: () => tone({ freq: 660, duration: 0.06, type: "triangle", volume: 0.1 }),
  correct: () => {
    tone({ freq: PENTA[2], duration: 0.14, type: "sine" });
    tone({ freq: PENTA[4], duration: 0.2, delay: 0.09, type: "sine" });
  },
  // "Error" nunca suena a castigo: es una nota grave y breve que solo invita a
  // volver a intentar. Es el control del error, no un regaño.
  wrong: () => tone({ freq: 300, duration: 0.16, type: "sine", volume: 0.12, slideTo: 220 }),
  star: () => {
    tone({ freq: PENTA[3], duration: 0.1, type: "triangle", volume: 0.14 });
    tone({ freq: PENTA[5], duration: 0.16, delay: 0.07, type: "triangle", volume: 0.14 });
  },
  win: () => {
    [0, 1, 2, 4, 5].forEach((n, i) =>
      tone({ freq: PENTA[n], duration: 0.24, delay: i * 0.1, type: "sine", volume: 0.16 })
    );
  },
};

let muted = false;
if (typeof window !== "undefined") {
  muted = !getSettings().sonido;
  subscribeSettings((s) => {
    muted = !s.sonido;
  });
}

export function playSound(name: SoundEffect) {
  if (typeof window === "undefined" || muted) return;
  if (preferFiles) {
    const audio = new Audio(FILES[name]);
    audio.volume = 0.7;
    audio.play().catch(() => SYNTH[name]());
    return;
  }
  try {
    SYNTH[name]();
  } catch {
    // Algunos navegadores bloquean WebAudio antes del primer toque; se ignora.
  }
}

/** Melodía ascendente para celebrar el fin de una etapa completa. */
export function playFanfare() {
  if (typeof window === "undefined" || muted) return;
  [0, 2, 4, 5, 4, 5].forEach((n, i) =>
    tone({ freq: PENTA[n], duration: 0.3, delay: i * 0.13, type: "triangle", volume: 0.15 })
  );
}

/** Nota de la escala, para juegos como "sigue el patrón" o las campanas. */
export function playNote(index: number, duration = 0.35) {
  if (typeof window === "undefined" || muted) return;
  const scale = [261.63, 293.66, 329.63, 349.23, 392.0, 440.0, 493.88, 523.25];
  tone({ freq: scale[index % scale.length], duration, type: "sine", volume: 0.2 });
}
