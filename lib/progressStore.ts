import { create } from "zustand";
import { persist } from "zustand/middleware";
import { LEVEL_COUNT, STAGE_SIZE, starsForLevel } from "./levels";

export interface GameProgress {
  stars: number;
  unlockedLevel: number;
  timesPlayed: number;
  /** Niveles ya completados al menos una vez. Permite repetir sin perder nada. */
  completed: number[];
  lastLevel: number;
  lastPlayedAt: number;
}

interface ProgressState {
  games: Record<string, GameProgress>;
  addStars: (gameId: string, amount: number) => void;
  unlockNextLevel: (gameId: string, level: number) => void;
  registerPlay: (gameId: string) => void;
  /** Marca el nivel como logrado, otorga estrellas y desbloquea el siguiente. */
  completeLevel: (gameId: string, level: number) => number;
  getProgress: (gameId: string) => GameProgress;
  reset: (gameId?: string) => void;
}

const defaultProgress: GameProgress = {
  stars: 0,
  unlockedLevel: 1,
  timesPlayed: 0,
  completed: [],
  lastLevel: 1,
  lastPlayedAt: 0,
};

function withDefaults(p?: Partial<GameProgress>): GameProgress {
  return { ...defaultProgress, ...p, completed: p?.completed ?? [] };
}

export const useProgressStore = create<ProgressState>()(
  persist(
    (set, get) => ({
      games: {},
      addStars: (gameId, amount) =>
        set((state) => {
          const current = withDefaults(state.games[gameId]);
          return {
            games: {
              ...state.games,
              [gameId]: { ...current, stars: current.stars + amount },
            },
          };
        }),
      unlockNextLevel: (gameId, level) =>
        set((state) => {
          const current = withDefaults(state.games[gameId]);
          return {
            games: {
              ...state.games,
              [gameId]: {
                ...current,
                unlockedLevel: Math.min(LEVEL_COUNT, Math.max(current.unlockedLevel, level)),
              },
            },
          };
        }),
      registerPlay: (gameId) =>
        set((state) => {
          const current = withDefaults(state.games[gameId]);
          return {
            games: {
              ...state.games,
              [gameId]: {
                ...current,
                timesPlayed: current.timesPlayed + 1,
                lastPlayedAt: Date.now(),
              },
            },
          };
        }),
      completeLevel: (gameId, level) => {
        const current = withDefaults(get().games[gameId]);
        const primeraVez = !current.completed.includes(level);
        // Repetir un nivel siempre está bien, pero las estrellas se dan una vez:
        // así el incentivo es avanzar, no repetir el nivel 1 cien veces.
        const ganadas = primeraVez ? starsForLevel(level) : 0;
        set((state) => ({
          games: {
            ...state.games,
            [gameId]: {
              ...current,
              stars: current.stars + ganadas,
              completed: primeraVez ? [...current.completed, level] : current.completed,
              unlockedLevel: Math.min(LEVEL_COUNT, Math.max(current.unlockedLevel, level + 1)),
              lastLevel: level,
              lastPlayedAt: Date.now(),
            },
          },
        }));
        return ganadas;
      },
      getProgress: (gameId) => withDefaults(get().games[gameId]),
      reset: (gameId) =>
        set((state) =>
          gameId
            ? { games: { ...state.games, [gameId]: { ...defaultProgress } } }
            : { games: {} }
        ),
    }),
    {
      name: "romgame-progress",
      version: 2,
      migrate: (persisted) => {
        const state = persisted as { games?: Record<string, Partial<GameProgress>> };
        const games: Record<string, GameProgress> = {};
        for (const [id, p] of Object.entries(state?.games ?? {})) games[id] = withDefaults(p);
        return { games } as ProgressState;
      },
    }
  )
);

/** Cuántos niveles completó el niño en un material (0 a 100). */
export function nivelesCompletados(p: GameProgress): number {
  return p.completed.length;
}

/** Etapa actual (1 a 10) según el nivel más alto desbloqueado. */
export function etapaActual(p: GameProgress): number {
  return Math.min(10, Math.ceil(p.unlockedLevel / STAGE_SIZE));
}
