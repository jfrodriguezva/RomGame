import { create } from "zustand";
import { persist } from "zustand/middleware";

export interface Settings {
  sonido: boolean;
  voz: boolean;
  vibracion: boolean;
  /** Modo calma: quita fondos animados y confeti, deja la pantalla serena. */
  calma: boolean;
  /** Nombre del niño, solo para saludarlo. Nunca sale del dispositivo. */
  nombre: string;
  /**
   * Edad del niño, elegida en la primera pantalla antes de ver el resto de
   * la app. `null` significa que todavía no se eligió — dispara la
   * pantalla de selección de edad en vez del menú principal. Filtra qué
   * materiales se muestran (por su `edad` mínima), para que un niño de 2-3
   * años no se tope con contenido de lectoescritura pensado para 4-6.
   */
  edad: number | null;
}

interface SettingsState extends Settings {
  set: <K extends keyof Settings>(key: K, value: Settings[K]) => void;
  toggle: (key: "sonido" | "voz" | "vibracion" | "calma") => void;
}

export const useSettings = create<SettingsState>()(
  persist(
    (set) => ({
      sonido: true,
      voz: true,
      vibracion: true,
      calma: false,
      nombre: "",
      edad: null,
      set: (key, value) => set({ [key]: value } as Partial<SettingsState>),
      toggle: (key) => set((s) => ({ [key]: !s[key] } as Partial<SettingsState>)),
    }),
    { name: "montessori-ajustes" }
  )
);

/** Acceso fuera de React (lo usan audio, voz y vibración). */
export function getSettings(): Settings {
  return useSettings.getState();
}

export function subscribeSettings(fn: (s: Settings) => void): () => void {
  return useSettings.subscribe(fn);
}
