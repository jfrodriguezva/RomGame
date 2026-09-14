"use client";

import { useEffect, useState } from "react";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { CARA_LEVELS, NOMBRE_PARTE, type ParteId } from "@/data/levels/cara";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";

const ENCONTRADO = "#4ade80";

/**
 * Nomenclatura de la cara, tocando directo sobre el dibujo — no eligiendo
 * de una lista de tarjetas, como MaterialQuiz. Cada oreja/ojo/ceja/mejilla
 * comparte el mismo id: tocar cualquiera de los dos cuenta, porque aquí no
 * se enseña lateralidad, se enseña el nombre de la parte.
 */
export default function CaraPage() {
  const material = useMaterial<(typeof CARA_LEVELS)[number]>("cara", CARA_LEVELS);
  const { config, level, logrado, nota } = material;

  const [objetivo, setObjetivo] = useState<ParteId | null>(null);
  const [encontradas, setEncontradas] = useState<Set<ParteId>>(new Set());

  function elegirObjetivo(yaEncontradas: Set<ParteId>) {
    const restantes = config.activos.filter((p) => !yaEncontradas.has(p));
    if (restantes.length === 0) return null;
    const elegido = restantes[Math.floor(Math.random() * restantes.length)];
    hablar(`Toca ${NOMBRE_PARTE[elegido].nombre}`);
    return elegido;
  }

  useEffect(() => {
    const vacio = new Set<ParteId>();
    // Reinicia el progreso y elige el primer objetivo del nivel: sincroniza
    // con una prop que cambia, no es una derivación pura del render actual.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setEncontradas(vacio);
    setObjetivo(elegirObjetivo(vacio));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function tocar(parte: ParteId) {
    if (!config.activos.includes(parte) || encontradas.has(parte)) return;
    if (parte !== objetivo) {
      material.intento(`Esa no. Busca ${NOMBRE_PARTE[objetivo ?? parte].nombre}`);
      return;
    }
    const siguiente = new Set(encontradas).add(parte);
    setEncontradas(siguiente);
    if (siguiente.size === config.activos.length) {
      setObjetivo(null);
      setTimeout(() => material.completar(), 500);
    } else {
      material.acierto(`¡Sí, ${NOMBRE_PARTE[parte].nombre}!`);
      setObjetivo(elegirObjetivo(siguiente));
    }
  }

  const activo = (p: ParteId) => config.activos.includes(p);
  const col = (p: ParteId, base: string) => (encontradas.has(p) ? ENCONTRADO : base);
  const clic = (p: ParteId) => (activo(p) ? () => tocar(p) : undefined);
  const estilo = (p: ParteId) => ({ cursor: activo(p) ? "pointer" : "default" });

  return (
    <GameShell
      slug="cara"
      level={level}
      levels={CARA_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={objetivo ? `Toca ${NOMBRE_PARTE[objetivo].nombre} ${NOMBRE_PARTE[objetivo].emoji}` : "¡Completo!"}
      nota={nota}
    >
      <ConfettiOverlay show={logrado} slug="cara" />
      <StarReward
        slug="cara"
        show={logrado}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={material.repetir}
      />

      <div className="mx-auto mb-4 max-w-xs rounded-[2rem] bg-white p-4 shadow-sm ring-1 ring-black/5">
        <svg viewBox="0 0 200 210" className="h-auto w-full" role="img" aria-label="Cara">
          {activo("pelo") && (
            <path
              d="M20 100 Q20 18 100 14 Q180 18 180 100 Q180 55 100 44 Q20 55 20 100 Z"
              fill={col("pelo", "#7c4a2d")}
              onClick={clic("pelo")}
              style={estilo("pelo")}
            />
          )}
          <circle cx={100} cy={112} r={80} fill="#f4c9a0" stroke="#c98a5e" strokeWidth={2} />

          {activo("oreja") && (
            <>
              <ellipse
                cx={16}
                cy={116}
                rx={13}
                ry={22}
                fill={col("oreja", "#f4c9a0")}
                stroke="#c98a5e"
                strokeWidth={2}
                onClick={clic("oreja")}
                style={estilo("oreja")}
              />
              <ellipse
                cx={184}
                cy={116}
                rx={13}
                ry={22}
                fill={col("oreja", "#f4c9a0")}
                stroke="#c98a5e"
                strokeWidth={2}
                onClick={clic("oreja")}
                style={estilo("oreja")}
              />
            </>
          )}

          {activo("menton") && (
            <path
              d="M68 172 Q100 202 132 172 Q100 196 68 172 Z"
              fill={col("menton", "#e5a878")}
              opacity={0.6}
              onClick={clic("menton")}
              style={estilo("menton")}
            />
          )}

          {activo("mejilla") && (
            <>
              <circle
                cx={55}
                cy={142}
                r={13}
                fill={col("mejilla", "#f4a6a6")}
                opacity={0.75}
                onClick={clic("mejilla")}
                style={estilo("mejilla")}
              />
              <circle
                cx={145}
                cy={142}
                r={13}
                fill={col("mejilla", "#f4a6a6")}
                opacity={0.75}
                onClick={clic("mejilla")}
                style={estilo("mejilla")}
              />
            </>
          )}

          {activo("ceja") && (
            <>
              <path
                d="M52 90 Q68 76 86 88"
                stroke={col("ceja", "#5a3a22")}
                strokeWidth={7}
                strokeLinecap="round"
                fill="none"
                onClick={clic("ceja")}
                style={estilo("ceja")}
              />
              <path
                d="M114 88 Q132 76 148 90"
                stroke={col("ceja", "#5a3a22")}
                strokeWidth={7}
                strokeLinecap="round"
                fill="none"
                onClick={clic("ceja")}
                style={estilo("ceja")}
              />
            </>
          )}

          <circle
            cx={68}
            cy={108}
            r={12}
            fill="#fff"
            stroke={col("ojo", "#3f342c")}
            strokeWidth={2.5}
            onClick={clic("ojo")}
            style={estilo("ojo")}
          />
          <circle cx={68} cy={108} r={5} fill={col("ojo", "#3f342c")} onClick={clic("ojo")} style={estilo("ojo")} />
          <circle
            cx={132}
            cy={108}
            r={12}
            fill="#fff"
            stroke={col("ojo", "#3f342c")}
            strokeWidth={2.5}
            onClick={clic("ojo")}
            style={estilo("ojo")}
          />
          <circle
            cx={132}
            cy={108}
            r={5}
            fill={col("ojo", "#3f342c")}
            onClick={clic("ojo")}
            style={estilo("ojo")}
          />

          <path
            d="M96 118 Q90 142 100 148 Q110 142 104 118 Z"
            fill={col("nariz", "#e5a878")}
            stroke="#c98a5e"
            strokeWidth={1.5}
            onClick={clic("nariz")}
            style={estilo("nariz")}
          />

          <path
            d="M74 166 Q100 182 126 166"
            stroke={col("boca", "#b5654a")}
            strokeWidth={7}
            strokeLinecap="round"
            fill="none"
            onClick={clic("boca")}
            style={estilo("boca")}
          />
        </svg>
      </div>

      <p className="text-center text-sm text-stone-400">
        Encontradas: {encontradas.size} / {config.activos.length}
      </p>
    </GameShell>
  );
}
