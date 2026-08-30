/**
 * Voz en español.
 *
 * Montessori es un método oral antes que escrito: el niño escucha el sonido de
 * la letra ("mmm"), no su nombre ("eme"). Usamos la síntesis de voz del sistema
 * para que la app hable sin necesidad de grabar audio.
 */

import { getSettings } from "./settings";

let cached: SpeechSynthesisVoice | null = null;

function voz(): SpeechSynthesisVoice | null {
  if (typeof window === "undefined" || !window.speechSynthesis) return null;
  if (cached) return cached;
  const voces = window.speechSynthesis.getVoices();
  if (voces.length === 0) return null;
  cached =
    voces.find((v) => /^es[-_]MX/i.test(v.lang)) ??
    voces.find((v) => /^es[-_]US/i.test(v.lang)) ??
    voces.find((v) => /^es/i.test(v.lang)) ??
    null;
  return cached;
}

if (typeof window !== "undefined" && window.speechSynthesis) {
  window.speechSynthesis.onvoiceschanged = () => {
    cached = null;
    voz();
  };
}

export function hablar(texto: string, opciones: { rate?: number; pitch?: number } = {}) {
  if (typeof window === "undefined" || !window.speechSynthesis) return;
  if (!getSettings().voz) return;
  try {
    window.speechSynthesis.cancel();
    const u = new SpeechSynthesisUtterance(texto);
    const v = voz();
    if (v) u.voice = v;
    u.lang = v?.lang ?? "es-MX";
    // Despacio y con tono cálido: el ritmo importa tanto como la palabra.
    u.rate = opciones.rate ?? 0.85;
    u.pitch = opciones.pitch ?? 1.1;
    window.speechSynthesis.speak(u);
  } catch {
    // Si el dispositivo no tiene motor de voz, la app sigue funcionando igual.
  }
}

export function callar() {
  if (typeof window === "undefined" || !window.speechSynthesis) return;
  window.speechSynthesis.cancel();
}

/** Sonido fonético de una letra, no su nombre. "M" se dice "mmm", no "eme". */
const FONEMAS: Record<string, string> = {
  A: "a", B: "b", C: "c", D: "d", E: "e", F: "fff", G: "g", H: "muda",
  I: "i", J: "j", K: "k", L: "lll", M: "mmm", N: "nnn", "Ñ": "ñ", O: "o",
  P: "p", Q: "k", R: "rrr", S: "sss", T: "t", U: "u", V: "b", W: "u",
  X: "ks", Y: "i", Z: "s",
};

export function fonemaDe(letra: string): string {
  return FONEMAS[letra.toUpperCase()] ?? letra;
}

export function hablarFonema(letra: string) {
  hablar(fonemaDe(letra), { rate: 0.7 });
}
