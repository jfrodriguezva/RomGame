"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import Link from "next/link";
import { AnimatePresence, motion } from "framer-motion";
import {
  type DibujoGuardado,
  type Herramienta,
  type Punto,
  borrarDeGaleria,
  descargar,
  exportarPNG,
  guardarEnGaleria,
  leerGaleria,
  miniatura,
  rellenar,
  sellar,
  trazar,
} from "@/lib/pizarra";
import { playSound } from "@/lib/audio";
import { vibrar, HAPTIC } from "@/lib/haptics";
import { GUIAS, type Guia } from "@/data/guias";

// ---------------------------------------------------------------------------
// Materiales de la pizarra
// ---------------------------------------------------------------------------

const HERRAMIENTAS: Array<{ id: Herramienta; icono: string; nombre: string }> = [
  { id: "lapiz", icono: "✏️", nombre: "Lápiz" },
  { id: "crayon", icono: "🖍️", nombre: "Crayón" },
  { id: "marcador", icono: "🖊️", nombre: "Marcador" },
  { id: "neon", icono: "✨", nombre: "Neón" },
  { id: "aerosol", icono: "💨", nombre: "Aerosol" },
  { id: "relleno", icono: "🪣", nombre: "Pintar" },
  { id: "sello", icono: "🌟", nombre: "Sellos" },
  { id: "borrador", icono: "🧽", nombre: "Borrar" },
];

const COLORES = [
  "#2f2a26", "#e04a3f", "#f08a3c", "#f5c542", "#8bbf5a", "#3f9e6d",
  "#3fa7c4", "#3f6fb5", "#7a5ec4", "#c25aa6", "#e58fa8", "#8b5e3c",
  "#c9a87c", "#9aa5ad", "#ffffff", "#000000",
];

const GROSORES = [
  { valor: 4, etiqueta: "Fino" },
  { valor: 10, etiqueta: "Medio" },
  { valor: 22, etiqueta: "Grueso" },
  { valor: 44, etiqueta: "Enorme" },
];

const FONDOS = [
  { id: "papel", nombre: "Papel", base: "#fdfaf5", css: "" },
  { id: "blanco", nombre: "Blanco", base: "#ffffff", css: "" },
  {
    id: "cuadricula",
    nombre: "Cuadros",
    base: "#ffffff",
    css: "repeating-linear-gradient(#e8e2d8 0 1px, transparent 1px 32px), repeating-linear-gradient(90deg, #e8e2d8 0 1px, transparent 1px 32px)",
  },
  {
    id: "renglones",
    nombre: "Renglones",
    base: "#ffffff",
    css: "repeating-linear-gradient(#dfe6ef 0 1px, transparent 1px 44px)",
  },
  {
    id: "puntos",
    nombre: "Puntos",
    base: "#fdfaf5",
    css: "radial-gradient(#ddd5c8 1.5px, transparent 1.6px)",
  },
  { id: "pizarron", nombre: "Pizarrón", base: "#26382f", css: "" },
  { id: "kraft", nombre: "Kraft", base: "#d8c0a0", css: "" },
] as const;

const SELLOS = [
  "⭐", "🌸", "🦋", "🐞", "🌈", "☁️", "🌞", "🌙",
  "🐢", "🐝", "🍎", "🌵", "🐳", "🎈", "🍀", "❤️",
];

const SIMETRIAS = [1, 2, 4, 6, 8];

export default function PizarraPage() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const ctxRef = useRef<CanvasRenderingContext2D | null>(null);
  const ultimoPunto = useRef<Punto | null>(null);
  const dibujando = useRef(false);

  const [herramienta, setHerramienta] = useState<Herramienta>("crayon");
  const [color, setColor] = useState("#e04a3f");
  const [grosor, setGrosor] = useState(10);
  const [sello, setSello] = useState("⭐");
  const [fondo, setFondo] = useState<(typeof FONDOS)[number]["id"]>("papel");
  const [simetria, setSimetria] = useState(1);
  const [guia, setGuia] = useState<Guia | null>(null);
  const [panel, setPanel] = useState<"pincel" | "fondo" | "guia" | "galeria" | null>("pincel");
  const [galeria, setGaleria] = useState<DibujoGuardado[]>([]);
  const [aviso, setAviso] = useState<string | null>(null);

  const historia = useRef<string[]>([]);
  const futuro = useRef<string[]>([]);
  const [puedeDeshacer, setPuedeDeshacer] = useState(false);
  const [puedeRehacer, setPuedeRehacer] = useState(false);

  const fondoActual = FONDOS.find((f) => f.id === fondo) ?? FONDOS[0];
  const esOscuro = fondo === "pizarron";

  // -------------------------------------------------------------------------
  // Lienzo
  // -------------------------------------------------------------------------

  const ajustarTamano = useCallback(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const dpr = Math.min(window.devicePixelRatio || 1, 2);
    const w = Math.round(rect.width * dpr);
    const h = Math.round(rect.height * dpr);
    if (canvas.width === w && canvas.height === h) return;

    // Al girar el dispositivo no se pierde el dibujo: se vuelve a pintar.
    const previo = canvas.width > 0 ? canvas.toDataURL() : null;
    canvas.width = w;
    canvas.height = h;
    const ctx = canvas.getContext("2d", { willReadFrequently: true });
    ctxRef.current = ctx;
    if (ctx) {
      ctx.scale(1, 1);
      if (previo) {
        const img = new Image();
        img.onload = () => ctx.drawImage(img, 0, 0, w, h);
        img.src = previo;
      }
    }
  }, []);

  useEffect(() => {
    ajustarTamano();
    const obs = new ResizeObserver(ajustarTamano);
    if (canvasRef.current) obs.observe(canvasRef.current);
    setGaleria(leerGaleria());
    return () => obs.disconnect();
  }, [ajustarTamano]);

  function coordenadas(e: React.PointerEvent<HTMLCanvasElement>): Punto {
    const canvas = canvasRef.current!;
    const rect = canvas.getBoundingClientRect();
    const escalaX = canvas.width / rect.width;
    const escalaY = canvas.height / rect.height;
    return {
      x: (e.clientX - rect.left) * escalaX,
      y: (e.clientY - rect.top) * escalaY,
      presion: e.pressure > 0 && e.pressure !== 0.5 ? e.pressure : 0.5,
    };
  }

  /** Aplica el trazo tantas veces como ejes tenga la simetría activa. */
  function conSimetria(a: Punto, b: Punto, pintar: (a: Punto, b: Punto) => void) {
    const canvas = canvasRef.current!;
    const cx = canvas.width / 2;
    const cy = canvas.height / 2;
    for (let i = 0; i < simetria; i++) {
      const ang = (Math.PI * 2 * i) / simetria;
      const rot = (p: Punto): Punto => {
        const dx = p.x - cx;
        const dy = p.y - cy;
        return {
          x: cx + dx * Math.cos(ang) - dy * Math.sin(ang),
          y: cy + dx * Math.sin(ang) + dy * Math.cos(ang),
          presion: p.presion,
        };
      };
      pintar(rot(a), rot(b));
      if (simetria > 1) {
        // El espejo cierra el mandala: cada eje se refleja sobre la vertical.
        const esp = (p: Punto): Punto => ({ ...rot(p), x: canvas.width - rot(p).x });
        pintar(esp(a), esp(b));
      }
    }
  }

  function guardarPaso() {
    const canvas = canvasRef.current;
    if (!canvas) return;
    historia.current.push(canvas.toDataURL());
    if (historia.current.length > 14) historia.current.shift();
    futuro.current = [];
    setPuedeDeshacer(true);
    setPuedeRehacer(false);
  }

  function pintarDesde(dataUrl: string | undefined) {
    const canvas = canvasRef.current;
    const ctx = ctxRef.current;
    if (!canvas || !ctx) return;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    if (!dataUrl) return;
    const img = new Image();
    img.onload = () => ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
    img.src = dataUrl;
  }

  function deshacer() {
    const canvas = canvasRef.current;
    if (!canvas || historia.current.length === 0) return;
    futuro.current.push(canvas.toDataURL());
    const anterior = historia.current.pop();
    pintarDesde(anterior);
    playSound("click");
    vibrar(HAPTIC.toque);
    setPuedeDeshacer(historia.current.length > 0);
    setPuedeRehacer(true);
  }

  function rehacer() {
    const canvas = canvasRef.current;
    if (!canvas || futuro.current.length === 0) return;
    historia.current.push(canvas.toDataURL());
    pintarDesde(futuro.current.pop());
    playSound("click");
    setPuedeDeshacer(true);
    setPuedeRehacer(futuro.current.length > 0);
  }

  // -------------------------------------------------------------------------
  // Gestos
  // -------------------------------------------------------------------------

  function alPresionar(e: React.PointerEvent<HTMLCanvasElement>) {
    const ctx = ctxRef.current;
    if (!ctx) return;
    e.currentTarget.setPointerCapture(e.pointerId);
    const p = coordenadas(e);
    guardarPaso();

    if (herramienta === "relleno") {
      rellenar(ctx, p.x, p.y, color);
      playSound("star");
      vibrar(HAPTIC.toque);
      return;
    }
    if (herramienta === "sello") {
      conSimetria(p, p, (a) => sellar(ctx, a, sello, grosor * 3.4));
      playSound("star");
      vibrar(HAPTIC.toque);
      return;
    }

    dibujando.current = true;
    ultimoPunto.current = p;
    // Un toque suelto también deja marca: el punto es un trazo válido.
    conSimetria(p, { ...p, x: p.x + 0.6 }, (a, b) =>
      trazar(ctx, a, b, { herramienta, color, grosor })
    );
  }

  function alMover(e: React.PointerEvent<HTMLCanvasElement>) {
    if (!dibujando.current || !ctxRef.current) return;
    const ctx = ctxRef.current;
    // Los eventos fusionados dan un trazo mas suave cuando el dedo va rapido,
    // pero no todos los WebView los entregan: si vienen vacios, se usa el
    // evento tal cual o el trazo simplemente no aparece.
    const fusionados =
      typeof e.nativeEvent.getCoalescedEvents === "function"
        ? e.nativeEvent.getCoalescedEvents()
        : [];
    const eventos = fusionados.length > 0 ? fusionados : [e.nativeEvent];

    for (const ev of eventos) {
      const canvas = canvasRef.current!;
      const rect = canvas.getBoundingClientRect();
      const p: Punto = {
        x: (ev.clientX - rect.left) * (canvas.width / rect.width),
        y: (ev.clientY - rect.top) * (canvas.height / rect.height),
        presion: ev.pressure > 0 && ev.pressure !== 0.5 ? ev.pressure : 0.5,
      };
      const previo = ultimoPunto.current ?? p;
      conSimetria(previo, p, (a, b) => trazar(ctx, a, b, { herramienta, color, grosor }));
      ultimoPunto.current = p;
    }
  }

  function alSoltar() {
    dibujando.current = false;
    ultimoPunto.current = null;
  }

  // -------------------------------------------------------------------------
  // Acciones
  // -------------------------------------------------------------------------

  function limpiar() {
    guardarPaso();
    const canvas = canvasRef.current;
    const ctx = ctxRef.current;
    if (canvas && ctx) ctx.clearRect(0, 0, canvas.width, canvas.height);
    mostrarAviso("Hoja nueva");
  }

  function guardar() {
    const canvas = canvasRef.current;
    if (!canvas) return;
    setGaleria(guardarEnGaleria(miniatura(canvas, fondoActual.base)));
    playSound("win");
    vibrar(HAPTIC.logro);
    mostrarAviso("Guardado en la galería");
  }

  async function compartir() {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ok = await descargar(exportarPNG(canvas, fondoActual.base));
    mostrarAviso(ok ? "Listo" : "No se pudo guardar el archivo");
  }

  function abrirDibujo(d: DibujoGuardado) {
    guardarPaso();
    pintarDesde(d.imagen);
    setPanel(null);
    mostrarAviso("Dibujo abierto");
  }

  function mostrarAviso(texto: string) {
    setAviso(texto);
    setTimeout(() => setAviso(null), 1600);
  }

  // -------------------------------------------------------------------------

  return (
    <div className="fixed inset-0 flex flex-col overflow-hidden bg-stone-100">
      {/* Barra superior */}
      <div className="absolute inset-x-0 top-0 z-30 flex items-center justify-between gap-2 px-2 py-2">
        <Link
          href="/"
          className="flex h-10 w-10 items-center justify-center rounded-2xl bg-white/90 text-lg shadow-sm ring-1 ring-black/5 active:scale-90"
          aria-label="Volver"
        >
          ←
        </Link>

        <div className="flex items-center gap-1.5">
          <BotonBarra onClick={deshacer} activo={puedeDeshacer} etiqueta="Deshacer">
            ↩︎
          </BotonBarra>
          <BotonBarra onClick={rehacer} activo={puedeRehacer} etiqueta="Rehacer">
            ↪︎
          </BotonBarra>
          <BotonBarra onClick={guardar} activo etiqueta="Guardar">
            💾
          </BotonBarra>
          <BotonBarra onClick={compartir} activo etiqueta="Compartir">
            📤
          </BotonBarra>
          <BotonBarra onClick={limpiar} activo etiqueta="Hoja nueva">
            🗑️
          </BotonBarra>
          <BotonBarra
            onClick={() => setPanel((p) => (p === null ? "pincel" : null))}
            activo
            etiqueta={panel ? "Ocultar herramientas" : "Mostrar herramientas"}
          >
            {panel ? "▾" : "▴"}
          </BotonBarra>
        </div>
      </div>

      {/* Lienzo */}
      <div
        className="relative flex-1"
        style={{
          backgroundColor: fondoActual.base,
          backgroundImage: fondoActual.css || undefined,
          backgroundSize: fondo === "puntos" ? "26px 26px" : undefined,
        }}
      >
        {guia && (
          <svg
            viewBox="0 0 100 100"
            preserveAspectRatio="xMidYMid meet"
            className="pointer-events-none absolute inset-0 h-full w-full p-10 opacity-30"
            aria-hidden
          >
            {guia.tipo === "texto" ? (
              <text
                x="50"
                y="50"
                textAnchor="middle"
                dominantBaseline="central"
                fontSize="62"
                fontWeight="700"
                fill="none"
                stroke={esOscuro ? "#ffffff" : "#8a7f70"}
                strokeWidth="1.1"
                strokeDasharray="3 3"
              >
                {guia.contenido}
              </text>
            ) : (
              <path
                d={guia.contenido}
                fill="none"
                stroke={esOscuro ? "#ffffff" : "#8a7f70"}
                strokeWidth="1.1"
                strokeDasharray="3 3"
                strokeLinecap="round"
              />
            )}
          </svg>
        )}

        <canvas
          ref={canvasRef}
          className="lienzo absolute inset-0 h-full w-full"
          onPointerDown={alPresionar}
          onPointerMove={alMover}
          onPointerUp={alSoltar}
          onPointerCancel={alSoltar}
          onPointerLeave={alSoltar}
        />

        <AnimatePresence>
          {aviso && (
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0 }}
              className="pointer-events-none absolute inset-x-0 top-16 flex justify-center"
            >
              <span className="rounded-full bg-stone-800/80 px-4 py-1.5 text-sm font-bold text-white">
                {aviso}
              </span>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* Cajón de herramientas */}
      <AnimatePresence initial={false}>
        {panel && (
          <motion.div
            initial={{ y: 220 }}
            animate={{ y: 0 }}
            exit={{ y: 220 }}
            transition={{ type: "spring", stiffness: 320, damping: 34 }}
            className="z-30 border-t border-black/5 bg-white/95 pb-[env(safe-area-inset-bottom)] backdrop-blur"
          >
            <div className="flex gap-1 overflow-x-auto px-3 pt-2 text-xs font-bold scroll-suave">
              {(
                [
                  ["pincel", "Pincel"],
                  ["fondo", "Hoja"],
                  ["guia", "Guías"],
                  ["galeria", "Galería"],
                ] as const
              ).map(([id, nombre]) => (
                <button
                  key={id}
                  onClick={() => setPanel(id)}
                  className={`rounded-full px-3 py-1.5 ${
                    panel === id ? "bg-stone-700 text-white" : "bg-stone-100 text-stone-500"
                  }`}
                >
                  {nombre}
                </button>
              ))}
            </div>

            <div className="max-h-[42vh] overflow-y-auto px-3 pb-3 pt-2 scroll-suave">
              {panel === "pincel" && (
                <>
                  <Fila>
                    {HERRAMIENTAS.map((h) => (
                      <button
                        key={h.id}
                        onClick={() => {
                          setHerramienta(h.id);
                          playSound("click");
                        }}
                        aria-label={h.nombre}
                        className={`flex h-14 w-14 shrink-0 flex-col items-center justify-center gap-0.5 rounded-2xl text-xl ${
                          herramienta === h.id
                            ? "bg-stone-700 text-white shadow"
                            : "bg-stone-100 text-stone-600"
                        }`}
                      >
                        <span>{h.icono}</span>
                        <span className="text-[9px] font-bold">{h.nombre}</span>
                      </button>
                    ))}
                  </Fila>

                  {herramienta === "sello" ? (
                    <Fila>
                      {SELLOS.map((s) => (
                        <button
                          key={s}
                          onClick={() => setSello(s)}
                          className={`h-11 w-11 shrink-0 rounded-2xl text-2xl ${
                            sello === s ? "bg-stone-200 ring-2 ring-stone-500" : "bg-stone-50"
                          }`}
                        >
                          {s}
                        </button>
                      ))}
                    </Fila>
                  ) : (
                    <Fila>
                      {COLORES.map((c) => (
                        <button
                          key={c}
                          onClick={() => {
                            setColor(c);
                            playSound("click");
                          }}
                          aria-label={`Color ${c}`}
                          className={`h-11 w-11 shrink-0 rounded-full ring-1 ring-black/10 transition ${
                            color === c ? "scale-110 ring-4 ring-stone-500" : ""
                          }`}
                          style={{ backgroundColor: c }}
                        />
                      ))}
                    </Fila>
                  )}

                  <Fila>
                    {GROSORES.map((g) => (
                      <button
                        key={g.valor}
                        onClick={() => setGrosor(g.valor)}
                        className={`flex h-12 shrink-0 items-center gap-2 rounded-2xl px-3 ${
                          grosor === g.valor ? "bg-stone-700 text-white" : "bg-stone-100 text-stone-600"
                        }`}
                      >
                        <span
                          className="rounded-full bg-current"
                          style={{ width: g.valor / 1.6 + 4, height: g.valor / 1.6 + 4 }}
                        />
                        <span className="text-xs font-bold">{g.etiqueta}</span>
                      </button>
                    ))}
                  </Fila>

                  <div className="mt-1 flex items-center gap-2">
                    <span className="text-xs font-bold text-stone-400">Mandala</span>
                    {SIMETRIAS.map((s) => (
                      <button
                        key={s}
                        onClick={() => setSimetria(s)}
                        className={`h-9 w-9 rounded-xl text-sm font-extrabold ${
                          simetria === s ? "bg-stone-700 text-white" : "bg-stone-100 text-stone-500"
                        }`}
                      >
                        {s === 1 ? "—" : s}
                      </button>
                    ))}
                  </div>
                </>
              )}

              {panel === "fondo" && (
                <Fila>
                  {FONDOS.map((f) => (
                    <button
                      key={f.id}
                      onClick={() => setFondo(f.id)}
                      className={`flex h-16 w-20 shrink-0 flex-col items-center justify-end overflow-hidden rounded-2xl pb-1 text-[10px] font-bold text-stone-600 ring-1 ring-black/10 ${
                        fondo === f.id ? "ring-4 ring-stone-500" : ""
                      }`}
                      style={{
                        backgroundColor: f.base,
                        backgroundImage: f.css || undefined,
                        backgroundSize: f.id === "puntos" ? "12px 12px" : undefined,
                      }}
                    >
                      <span className="rounded bg-white/80 px-1">{f.nombre}</span>
                    </button>
                  ))}
                </Fila>
              )}

              {panel === "guia" && (
                <>
                  <p className="mb-2 text-xs text-stone-500">
                    Una figura punteada para repasar con el dedo. Se queda debajo del dibujo y no
                    se borra.
                  </p>
                  <Fila>
                    <button
                      onClick={() => setGuia(null)}
                      className={`h-12 shrink-0 rounded-2xl px-4 text-xs font-bold ${
                        guia === null ? "bg-stone-700 text-white" : "bg-stone-100 text-stone-500"
                      }`}
                    >
                      Sin guía
                    </button>
                    {GUIAS.map((g) => (
                      <button
                        key={g.id}
                        onClick={() => setGuia(g)}
                        className={`h-12 min-w-12 shrink-0 rounded-2xl px-3 text-lg font-extrabold ${
                          guia?.id === g.id
                            ? "bg-stone-700 text-white"
                            : "bg-stone-100 text-stone-600"
                        }`}
                        aria-label={g.nombre}
                      >
                        {g.icono}
                      </button>
                    ))}
                  </Fila>
                </>
              )}

              {panel === "galeria" && (
                <>
                  {galeria.length === 0 ? (
                    <p className="py-6 text-center text-sm text-stone-400">
                      Aún no hay dibujos guardados. Usa 💾 para guardar el que estás haciendo.
                    </p>
                  ) : (
                    <Fila>
                      {galeria.map((d) => (
                        <div key={d.id} className="relative shrink-0">
                          <button onClick={() => abrirDibujo(d)}>
                            {/* eslint-disable-next-line @next/next/no-img-element */}
                            <img
                              src={d.imagen}
                              alt="Dibujo guardado"
                              className="h-24 w-32 rounded-xl object-cover ring-1 ring-black/10"
                            />
                          </button>
                          <button
                            onClick={() => setGaleria(borrarDeGaleria(d.id))}
                            className="absolute -right-1 -top-1 h-6 w-6 rounded-full bg-white text-xs shadow ring-1 ring-black/10"
                            aria-label="Borrar dibujo"
                          >
                            ✕
                          </button>
                        </div>
                      ))}
                    </Fila>
                  )}
                </>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

function Fila({ children }: { children: React.ReactNode }) {
  return (
    <div className="mb-2 flex gap-2 overflow-x-auto pb-1 scroll-suave">{children}</div>
  );
}

function BotonBarra({
  children,
  onClick,
  activo,
  etiqueta,
}: {
  children: React.ReactNode;
  onClick: () => void;
  activo: boolean;
  etiqueta: string;
}) {
  return (
    <button
      onClick={onClick}
      disabled={!activo}
      aria-label={etiqueta}
      className="flex h-10 w-10 items-center justify-center rounded-2xl bg-white/90 text-base shadow-sm ring-1 ring-black/5 transition active:scale-90 disabled:opacity-35"
    >
      {children}
    </button>
  );
}
