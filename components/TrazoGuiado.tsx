"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { playSound } from "@/lib/audio";
import { vibrar, HAPTIC } from "@/lib/haptics";

interface Punto {
  x: number;
  y: number;
}

const ESPACIADO = 5; // unidades del lienzo entre punto y punto

/**
 * Trazo guiado, al modo de las letras de lija.
 *
 * El niño recorre el glifo con el dedo siguiendo los puntos en orden. No se
 * puede hacer trampa saltando al final, pero tampoco se castiga salirse: si
 * el dedo se va, simplemente el punto siguiente no se marca y hay que volver.
 * El material acompaña el movimiento, no lo evalúa.
 */
export default function TrazoGuiado({
  strokes,
  tolerancia = 10,
  mostrarPuntos = true,
  onCompleto,
  color = "#46578a",
}: {
  strokes: string[];
  tolerancia?: number;
  mostrarPuntos?: boolean;
  onCompleto: () => void;
  color?: string;
}) {
  const svgRef = useRef<SVGSVGElement>(null);
  const pathRefs = useRef<Array<SVGPathElement | null>>([]);
  const [puntos, setPuntos] = useState<Punto[][]>([]);
  const [trazo, setTrazo] = useState(0);
  const [avance, setAvance] = useState(0);
  const dibujando = useRef(false);

  // Se recalculan los puntos cada vez que cambia el glifo.
  useEffect(() => {
    const medidos = strokes.map((_, i) => {
      const path = pathRefs.current[i];
      if (!path) return [];
      const largo = path.getTotalLength();
      const cantidad = Math.max(3, Math.round(largo / ESPACIADO));
      return Array.from({ length: cantidad + 1 }, (_, k) => {
        const p = path.getPointAtLength((largo * k) / cantidad);
        return { x: p.x, y: p.y };
      });
    });
    setPuntos(medidos);
    setTrazo(0);
    setAvance(0);
  }, [strokes]);

  const aLienzo = useCallback((clientX: number, clientY: number): Punto | null => {
    const svg = svgRef.current;
    if (!svg) return null;
    const rect = svg.getBoundingClientRect();
    return {
      x: ((clientX - rect.left) / rect.width) * 100,
      y: ((clientY - rect.top) / rect.height) * 100,
    };
  }, []);

  const avanzar = useCallback(
    (p: Punto) => {
      const actuales = puntos[trazo];
      if (!actuales || actuales.length === 0) return;

      let indice = avance;
      // Se admite adelantar varios puntos de golpe: los dedos van rápido.
      while (indice < actuales.length) {
        const objetivo = actuales[indice];
        const d = Math.hypot(objetivo.x - p.x, objetivo.y - p.y);
        if (d > tolerancia) break;
        indice++;
      }

      if (indice === avance) return;
      setAvance(indice);

      if (indice >= actuales.length) {
        const siguienteTrazo = trazo + 1;
        vibrar(HAPTIC.toque);
        if (siguienteTrazo >= puntos.length) {
          playSound("correct");
          onCompleto();
        } else {
          playSound("click");
          setTrazo(siguienteTrazo);
          setAvance(0);
        }
      }
    },
    [avance, onCompleto, puntos, tolerancia, trazo]
  );

  function alPresionar(e: React.PointerEvent<SVGSVGElement>) {
    e.currentTarget.setPointerCapture(e.pointerId);
    dibujando.current = true;
    const p = aLienzo(e.clientX, e.clientY);
    if (p) avanzar(p);
  }

  function alMover(e: React.PointerEvent<SVGSVGElement>) {
    if (!dibujando.current) return;
    const p = aLienzo(e.clientX, e.clientY);
    if (p) avanzar(p);
  }

  function alSoltar() {
    dibujando.current = false;
  }

  const puntosActuales = puntos[trazo] ?? [];
  const siguiente = puntosActuales[Math.min(avance, puntosActuales.length - 1)];

  return (
    <svg
      ref={svgRef}
      viewBox="0 0 100 100"
      className="lienzo h-full w-full touch-none"
      onPointerDown={alPresionar}
      onPointerMove={alMover}
      onPointerUp={alSoltar}
      onPointerCancel={alSoltar}
      role="img"
      aria-label="Traza la letra siguiendo los puntos"
    >
      {strokes.map((d, i) => {
        const hecho = i < trazo;
        const actual = i === trazo;
        const total = puntos[i]?.length ?? 1;
        return (
          <g key={i}>
            {/* Guía tenue del trazo completo */}
            <path
              ref={(el) => {
                pathRefs.current[i] = el;
              }}
              d={d}
              fill="none"
              stroke="#d9d2c7"
              strokeWidth={9}
              strokeLinecap="round"
              strokeLinejoin="round"
            />
            {/* Lo que ya se recorrió, en color */}
            <path
              d={d}
              fill="none"
              stroke={color}
              strokeWidth={9}
              strokeLinecap="round"
              strokeLinejoin="round"
              pathLength={100}
              strokeDasharray={100}
              strokeDashoffset={hecho ? 0 : actual ? 100 - (avance / total) * 100 : 100}
              style={{ transition: "stroke-dashoffset 90ms linear" }}
            />
          </g>
        );
      })}

      {mostrarPuntos &&
        puntosActuales
          .filter((_, i) => i % 3 === 0 && i >= avance)
          .map((p, i) => (
            <circle key={i} cx={p.x} cy={p.y} r={1.4} fill="#b6ada0" />
          ))}

      {/* El punto por donde va: dónde poner el dedo ahora */}
      {siguiente && avance < puntosActuales.length && (
        <circle
          cx={siguiente.x}
          cy={siguiente.y}
          r={3.4}
          fill={color}
          style={{ animation: "respira 1.4s ease-in-out infinite", transformOrigin: "center" }}
        />
      )}

      {/* La flecha de inicio solo aparece al empezar un trazo */}
      {avance === 0 && siguiente && (
        <text x={siguiente.x} y={siguiente.y - 6} textAnchor="middle" fontSize="7" fill={color}>
          ▾
        </text>
      )}
    </svg>
  );
}
