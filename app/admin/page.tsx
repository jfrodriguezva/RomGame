"use client";

import { useState } from "react";
import BackHomeButton from "@/components/BackHomeButton";

interface Point {
  id: number;
  x: number;
  y: number;
  label: string;
}

let nextId = 0;

export default function AdminPage() {
  const [imagePath, setImagePath] = useState("/images/princesas/1.png");
  const [currentLabel, setCurrentLabel] = useState("");
  const [points, setPoints] = useState<Point[]>([]);
  const [copied, setCopied] = useState(false);

  function handleImageClick(e: React.MouseEvent<HTMLImageElement>) {
    const rect = e.currentTarget.getBoundingClientRect();
    const x = ((e.clientX - rect.left) / rect.width) * 100;
    const y = ((e.clientY - rect.top) / rect.height) * 100;
    setPoints((prev) => [
      ...prev,
      { id: nextId++, x: Math.round(x * 10) / 10, y: Math.round(y * 10) / 10, label: currentLabel || `punto-${prev.length + 1}` },
    ]);
  }

  function removePoint(id: number) {
    setPoints((prev) => prev.filter((p) => p.id !== id));
  }

  const generatedCode = JSON.stringify(
    {
      image: imagePath,
      points: points.map((p) => ({ label: p.label, x: p.x, y: p.y })),
    },
    null,
    2
  );

  async function copyCode() {
    await navigator.clipboard.writeText(generatedCode);
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-slate-100 via-white to-white pb-10">
      <BackHomeButton />
      <main className="mx-auto w-full max-w-3xl px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-slate-700 sm:text-3xl">
          🛠️ Editor de niveles (modo papá/mamá)
        </h1>
        <p className="mb-6 text-center text-sm text-slate-500">
          Marca sobre tu imagen los puntos que quieres usar en &quot;Diferencias&quot; o
          &quot;Encuentra los objetos&quot;. Al final, copia el código generado y pégalo donde te
          indique — por ahora es un paso manual, más adelante lo dejamos automático.
        </p>

        <div className="mb-4 flex flex-col gap-2 sm:flex-row sm:items-center">
          <label className="text-sm font-bold text-slate-600">Ruta de la imagen:</label>
          <input
            value={imagePath}
            onChange={(e) => setImagePath(e.target.value)}
            className="flex-1 rounded-xl border border-slate-300 px-3 py-2 text-sm"
            placeholder="/images/princesas/1.png"
          />
        </div>

        <div className="mb-4 flex flex-col gap-2 sm:flex-row sm:items-center">
          <label className="text-sm font-bold text-slate-600">Etiqueta del próximo punto:</label>
          <input
            value={currentLabel}
            onChange={(e) => setCurrentLabel(e.target.value)}
            className="flex-1 rounded-xl border border-slate-300 px-3 py-2 text-sm"
            placeholder="ej. corona, diferencia-1..."
          />
        </div>

        <div className="relative mb-4 w-full overflow-hidden rounded-2xl border border-slate-300 bg-slate-100">
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img
            src={imagePath}
            alt="Imagen a marcar"
            onClick={handleImageClick}
            className="w-full cursor-crosshair"
            onError={(e) => {
              (e.target as HTMLImageElement).style.opacity = "0.3";
            }}
          />
          {points.map((p) => (
            <div
              key={p.id}
              className="absolute flex -translate-x-1/2 -translate-y-1/2 flex-col items-center"
              style={{ left: `${p.x}%`, top: `${p.y}%` }}
            >
              <span className="flex h-6 w-6 items-center justify-center rounded-full bg-rose-500 text-xs font-bold text-white shadow">
                {points.indexOf(p) + 1}
              </span>
            </div>
          ))}
        </div>
        <p className="mb-6 text-xs text-slate-400">
          Si no ves la imagen es porque aún no la has colocado en /public/images/. Aun así puedes
          marcar puntos sobre el recuadro para probar el editor.
        </p>

        <div className="mb-6 flex flex-wrap gap-2">
          {points.map((p, i) => (
            <span
              key={p.id}
              className="flex items-center gap-2 rounded-full bg-white px-3 py-1 text-xs shadow"
            >
              #{i + 1} {p.label} ({p.x}%, {p.y}%)
              <button onClick={() => removePoint(p.id)} className="text-rose-500">
                ✕
              </button>
            </span>
          ))}
        </div>

        <div className="rounded-2xl bg-slate-900 p-4">
          <div className="mb-2 flex items-center justify-between">
            <span className="text-xs font-bold text-slate-300">Código generado</span>
            <button
              onClick={copyCode}
              className="rounded-full bg-emerald-500 px-3 py-1 text-xs font-bold text-white"
            >
              {copied ? "¡Copiado!" : "Copiar"}
            </button>
          </div>
          <pre className="overflow-x-auto text-xs text-emerald-300">{generatedCode}</pre>
        </div>
      </main>
    </div>
  );
}
