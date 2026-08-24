import { create } from "zustand";
import { persist } from "zustand/middleware";

interface GameProgress {
  stars: number;
  unlockedLevel: number;
  timesPlayed: number;
}

interface ProgressState {
  games: Record<string, GameProgress>;
  addStars: (gameId: string, amount: number) => void;
  unlockNextLevel: (gameId: string, level: number) => void;
  registerPlay: (gameId: string) => void;
  getProgress: (gameId: string) => GameProgress;
}

const defaultProgress: GameProgress = {
  stars: 0,
  unlockedLevel: 1,
  timesPlayed: 0,
};

export const useProgressStore = create<ProgressState>()(
  persist(
    (set, get) => ({
      games: {},
      addStars: (gameId, amount) =>
        set((state) => {
          const current = state.games[gameId] ?? defaultProgress;
          return {
            games: {
              ...state.games,
              [gameId]: { ...current, stars: current.stars + amount },
            },
          };
        }),
      unlockNextLevel: (gameId, level) =>
        set((state) => {
          const current = state.games[gameId] ?? defaultProgress;
          return {
            games: {
              ...state.games,
              [gameId]: {
                ...current,
                unlockedLevel: Math.max(current.unlockedLevel, level),
              },
            },
          };
        }),
      registerPlay: (gameId) =>
        set((state) => {
          const current = state.games[gameId] ?? defaultProgress;
          return {
            games: {
              ...state.games,
              [gameId]: { ...current, timesPlayed: current.timesPlayed + 1 },
            },
          };
        }),
      getProgress: (gameId) => get().games[gameId] ?? defaultProgress,
    }),
    { name: "romgame-progress" }
  )
);
