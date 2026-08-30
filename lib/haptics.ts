import { getSettings } from "./settings";

/**
 * Vibración corta. En el APK de Android el WebView soporta navigator.vibrate,
 * así que no hace falta un plugin nativo extra.
 */
export function vibrar(pattern: number | readonly number[] = 12) {
  if (typeof navigator === "undefined" || !("vibrate" in navigator)) return;
  if (!getSettings().vibracion) return;
  try {
    navigator.vibrate(typeof pattern === "number" ? pattern : [...pattern]);
  } catch {
    // Algunos dispositivos lo tienen deshabilitado; no es crítico.
  }
}

export const HAPTIC = {
  toque: 10,
  acierto: [12, 40, 18],
  error: 28,
  logro: [18, 60, 18, 60, 30],
} as const;
