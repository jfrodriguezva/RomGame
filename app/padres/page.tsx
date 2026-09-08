"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import BackHomeButton from "@/components/BackHomeButton";
import { games, gamesByArea, TOTAL_NIVELES } from "@/data/games";
import { AREAS, AREA_ORDER } from "@/lib/montessori";
import { LEVEL_COUNT, STAGES, stageOf } from "@/lib/levels";
import { useProgressStore } from "@/lib/progressStore";
import { useSettings } from "@/lib/settings";

/**
 * Vista de mamá y papá.
 *
 * No es un panel de control del niño: es una ventana para entender qué está
 * trabajando. Por eso cada material dice para qué sirve, y no hay ranking,
 * ni comparación con otros niños, ni metas por edad.
 */
export default function PadresPage() {
  const progreso = useProgressStore((s) => s.games);
  const reset = useProgressStore((s) => s.reset);
  const ajustes = useSettings();
  const [confirmando, setConfirmando] = useState(false);

  const resumen = useMemo(() => {
    const lista = Object.values(progreso);
    return {
      estrellas: lista.reduce((a, p) => a + p.stars, 0),
      niveles: lista.reduce((a, p) => a + p.completed.length, 0),
      sesiones: lista.reduce((a, p) => a + p.timesPlayed, 0),
      materiales: lista.filter((p) => p.timesPlayed > 0).length,
    };
  }, [progreso]);

  return (
    <div className="min-h-full flex-1 textura-papel bg-[#f6f3ed]">
      <BackHomeButton />

      <main className="mx-auto w-full max-w-2xl px-4 pb-20 pt-20 sm:px-6">
        <h1 className="mb-1 text-2xl font-extrabold text-stone-700">Mamá y papá</h1>
        <p className="mb-8 text-sm text-stone-500">
          Todo lo que ves aquí está guardado solo en este dispositivo. La app no envía nada a
          ningún servidor, no pide permisos y no tiene publicidad.
        </p>

        {/* Resumen */}
        <section className="mb-8 grid grid-cols-2 gap-3 sm:grid-cols-4">
          <Tarjeta valor={resumen.niveles} etiqueta="niveles logrados" />
          <Tarjeta valor={resumen.estrellas} etiqueta="estrellas" />
          <Tarjeta valor={resumen.materiales} etiqueta={`de ${games.length} materiales`} />
          <Tarjeta valor={resumen.sesiones} etiqueta="veces que ha trabajado" />
        </section>

        {/* Ajustes */}
        <section className="mb-10 rounded-3xl bg-white p-5 shadow-sm ring-1 ring-black/5">
          <h2 className="mb-4 text-base font-extrabold text-stone-600">Ajustes</h2>

          <label className="mb-4 block">
            <span className="mb-1 block text-xs font-bold uppercase tracking-wide text-stone-400">
              ¿Cómo se llama?
            </span>
            <input
              value={ajustes.nombre}
              onChange={(e) => ajustes.set("nombre", e.target.value.slice(0, 20))}
              placeholder="Solo para saludarlo al entrar"
              className="w-full rounded-2xl border border-stone-200 px-4 py-2.5 text-base text-stone-700 outline-none focus:border-stone-400"
            />
          </label>

          <div className="grid gap-2">
            <Interruptor
              activo={ajustes.sonido}
              onChange={() => ajustes.toggle("sonido")}
              titulo="Sonidos"
              detalle="Tonos suaves al acertar y al terminar."
            />
            <Interruptor
              activo={ajustes.voz}
              onChange={() => ajustes.toggle("voz")}
              titulo="Voz"
              detalle="La app dice los sonidos de las letras y los nombres. Usa la voz del sistema."
            />
            <Interruptor
              activo={ajustes.vibracion}
              onChange={() => ajustes.toggle("vibracion")}
              titulo="Vibración"
              detalle="Un toque corto al colocar bien una pieza."
            />
            <Interruptor
              activo={ajustes.calma}
              onChange={() => ajustes.toggle("calma")}
              titulo="Modo calma"
              detalle="Quita el fondo animado y el confeti. Útil si se distrae o se sobreestimula."
            />
          </div>
        </section>

        {/* Progreso por área */}
        <h2 className="mb-3 text-base font-extrabold text-stone-600">Qué está trabajando</h2>
        <div className="mb-10 space-y-6">
          {AREA_ORDER.map((areaId) => {
            const area = AREAS[areaId];
            const lista = gamesByArea(areaId);
            const conNiveles = lista.filter((g) => !g.libre);
            const hechos = conNiveles.reduce(
              (a, g) => a + (progreso[g.id]?.completed.length ?? 0),
              0
            );
            const posibles = conNiveles.length * LEVEL_COUNT;

            return (
              <section key={areaId} className="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-black/5">
                <div className="mb-1 flex items-center gap-2">
                  <span className="text-lg">{area.emoji}</span>
                  <h3 className={`text-sm font-extrabold ${area.text}`}>{area.label}</h3>
                  {posibles > 0 && (
                    <span className="ml-auto text-xs font-bold text-stone-400">
                      {hechos} / {posibles}
                    </span>
                  )}
                </div>
                <p className="mb-4 text-xs text-stone-500">{area.proposito}</p>

                <div className="space-y-3">
                  {lista.map((g) => {
                    const p = progreso[g.id];
                    const completados = p?.completed.length ?? 0;
                    const etapa = p ? stageOf(p.unlockedLevel) : 1;
                    return (
                      <div key={g.id} className="border-t border-stone-100 pt-3 first:border-0 first:pt-0">
                        <div className="flex items-baseline gap-2">
                          <span>{g.emoji}</span>
                          <span className="text-sm font-bold text-stone-700">{g.title}</span>
                          <span className="ml-auto text-[11px] font-bold text-stone-400">
                            {g.libre
                              ? p?.timesPlayed
                                ? `${p.timesPlayed} veces`
                                : "sin usar"
                              : completados > 0
                                ? `nivel ${p?.unlockedLevel} · ${STAGES[etapa - 1].name}`
                                : "sin empezar"}
                          </span>
                        </div>
                        <p className="mt-0.5 text-xs text-stone-500">{g.objetivo}</p>
                        <p className="mt-0.5 text-[11px] text-stone-400">
                          Material: {g.material} · {g.edad[0]} a {g.edad[1]} años
                        </p>
                        {!g.libre && (
                          <div className="mt-1.5 h-1.5 overflow-hidden rounded-full bg-stone-100">
                            <div
                              className="h-full rounded-full bg-stone-400"
                              style={{ width: `${(completados / LEVEL_COUNT) * 100}%` }}
                            />
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              </section>
            );
          })}
        </div>

        {/* Cómo acompañar */}
        <section className="mb-10 rounded-3xl bg-[#eef3ec] p-5 text-sm leading-relaxed text-[#3f5c42]">
          <h2 className="mb-2 text-base font-extrabold">Cómo acompañar</h2>
          <ul className="list-disc space-y-1.5 pl-4">
            <li>Deje que elija el material. La elección libre es parte del método.</li>
            <li>
              Repetir no es retroceder. Un niño que hace veinte veces el mismo nivel está
              trabajando, no atorado.
            </li>
            <li>
              Cuando se equivoque, no lo corrija: el material ya lo hace. Basta con esperar.
            </li>
            <li>
              Mejor sesiones cortas y completas que largas y con prisa. Terminar el ciclo importa
              más que avanzar de nivel.
            </li>
            <li>
              Esta app no sustituye el material real. Si puede, ponga en sus manos objetos que
              pesen, rueden y se caigan.
            </li>
          </ul>
        </section>

        <section className="rounded-3xl bg-white p-5 shadow-sm ring-1 ring-black/5">
          <h2 className="mb-3 text-base font-extrabold text-stone-600">Herramientas</h2>
          <Link
            href="/admin"
            className="mb-4 inline-block rounded-2xl bg-stone-100 px-4 py-2.5 text-sm font-bold text-stone-600"
          >
            Editor de puntos para imágenes propias
          </Link>

          <div className="border-t border-stone-100 pt-4">
            {confirmando ? (
              <div className="flex flex-wrap items-center gap-2">
                <span className="text-sm text-stone-600">
                  Se borrará todo el progreso. ¿Seguro?
                </span>
                <button
                  onClick={() => {
                    reset();
                    setConfirmando(false);
                  }}
                  className="rounded-xl bg-[#b23b34] px-4 py-2 text-sm font-bold text-white"
                >
                  Sí, borrar
                </button>
                <button
                  onClick={() => setConfirmando(false)}
                  className="rounded-xl bg-stone-100 px-4 py-2 text-sm font-bold text-stone-500"
                >
                  Cancelar
                </button>
              </div>
            ) : (
              <button
                onClick={() => setConfirmando(true)}
                className="text-sm font-bold text-stone-400 underline"
              >
                Borrar todo el progreso
              </button>
            )}
          </div>
        </section>

        <p className="mt-8 text-center text-xs text-stone-400">
          {TOTAL_NIVELES.toLocaleString("es-MX")} niveles repartidos en {games.length} materiales.
        </p>
      </main>
    </div>
  );
}

function Tarjeta({ valor, etiqueta }: { valor: number; etiqueta: string }) {
  return (
    <div className="rounded-2xl bg-white p-4 text-center shadow-sm ring-1 ring-black/5">
      <span className="block text-2xl font-extrabold text-stone-700">{valor}</span>
      <span className="block text-[11px] font-bold uppercase tracking-wide text-stone-400">
        {etiqueta}
      </span>
    </div>
  );
}

function Interruptor({
  activo,
  onChange,
  titulo,
  detalle,
}: {
  activo: boolean;
  onChange: () => void;
  titulo: string;
  detalle: string;
}) {
  return (
    <button
      onClick={onChange}
      className="flex items-start gap-3 rounded-2xl bg-stone-50 p-3 text-left active:scale-[0.99]"
      role="switch"
      aria-checked={activo}
    >
      <span
        className={`mt-0.5 flex h-6 w-11 shrink-0 items-center rounded-full p-0.5 transition ${
          activo ? "bg-[#4a6b4d]" : "bg-stone-300"
        }`}
      >
        <span
          className={`h-5 w-5 rounded-full bg-white shadow transition ${
            activo ? "translate-x-5" : ""
          }`}
        />
      </span>
      <span>
        <span className="block text-sm font-bold text-stone-700">{titulo}</span>
        <span className="block text-xs text-stone-500">{detalle}</span>
      </span>
    </button>
  );
}
