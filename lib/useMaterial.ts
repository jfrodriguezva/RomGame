"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { playSound, playFanfare } from "./audio";
import { vibrar, HAPTIC } from "./haptics";
import { hablar } from "./speech";
import { fraseAcierto, fraseIntento, fraseNivelCompleto } from "./montessori";
import { useProgressStore } from "./progressStore";
import { STAGE_SIZE } from "./levels";

export interface Nota {
  texto: string;
  tipo: "bien" | "otra";
}

/**
 * Estado común a todos los materiales: nivel actual, control del error y
 * cierre del nivel. Al concentrarlo aquí, cada material solo se ocupa de su
 * propia mecánica y todos se comportan igual ante el acierto y el intento.
 */
export function useMaterial<T extends { level: number }>(
  gameId: string,
  levels: T[],
  opciones: { hablarAlLograr?: boolean } = {}
) {
  const [level, setLevelState] = useState(1);
  const [logrado, setLogrado] = useState(false);
  const [nota, setNota] = useState<Nota | null>(null);
  const [estrellas, setEstrellas] = useState(0);
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null);

  const registerPlay = useProgressStore((s) => s.registerPlay);
  const completeLevel = useProgressStore((s) => s.completeLevel);

  const indice = Math.min(levels.length, Math.max(1, level)) - 1;
  const config = levels[indice];

  useEffect(() => {
    registerPlay(gameId);
  }, [gameId, registerPlay]);

  useEffect(() => {
    return () => {
      if (timer.current) clearTimeout(timer.current);
    };
  }, []);

  const mostrar = useCallback((texto: string, tipo: Nota["tipo"]) => {
    setNota({ texto, tipo });
    if (timer.current) clearTimeout(timer.current);
    timer.current = setTimeout(() => setNota(null), 1500);
  }, []);

  /** Acierto parcial: una pieza quedó en su lugar, el nivel sigue. */
  const acierto = useCallback(
    (texto?: string) => {
      playSound("correct");
      vibrar(HAPTIC.acierto);
      mostrar(texto ?? fraseAcierto(Date.now() / 1000), "bien");
    },
    [mostrar]
  );

  /**
   * Control del error: la pieza vuelve a su sitio y se invita a mirar otra vez.
   * Nunca hay penalización, ni vidas, ni "perdiste".
   */
  const intento = useCallback(
    (texto?: string) => {
      playSound("wrong");
      vibrar(HAPTIC.error);
      mostrar(texto ?? fraseIntento(Date.now() / 1000), "otra");
    },
    [mostrar]
  );

  /** Cierra el nivel: otorga estrellas, desbloquea el siguiente y celebra. */
  const completar = useCallback(() => {
    const ganadas = completeLevel(gameId, level);
    setEstrellas(ganadas);
    setLogrado(true);
    vibrar(HAPTIC.logro);
    if (level % STAGE_SIZE === 0) playFanfare();
    else playSound("win");
    if (opciones.hablarAlLograr !== false) hablar(fraseNivelCompleto(level));
  }, [completeLevel, gameId, level, opciones.hablarAlLograr]);

  const irANivel = useCallback(
    (n: number) => {
      setLogrado(false);
      setNota(null);
      setLevelState(Math.min(levels.length, Math.max(1, n)));
    },
    [levels.length]
  );

  const siguiente = useCallback(() => irANivel(level + 1), [irANivel, level]);
  const repetir = useCallback(() => irANivel(level), [irANivel, level]);

  return {
    level,
    config,
    logrado,
    nota,
    estrellas,
    esUltimo: level >= levels.length,
    setLevel: irANivel,
    acierto,
    intento,
    completar,
    siguiente,
    repetir,
  };
}
