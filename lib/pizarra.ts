"use client";

/** Utilidades del lienzo: relleno por regiones, exportar y galería local. */

export interface Punto {
  x: number;
  y: number;
  presion: number;
}

export type Herramienta =
  | "lapiz"
  | "crayon"
  | "marcador"
  | "neon"
  | "aerosol"
  | "borrador"
  | "relleno"
  | "sello";

export interface Pincel {
  herramienta: Herramienta;
  color: string;
  grosor: number;
  sello?: string;
}

// ---------------------------------------------------------------------------
// Trazo
// ---------------------------------------------------------------------------

function jitter(n: number): number {
  return (Math.random() - 0.5) * n;
}

/** Dibuja un segmento del trazo con la textura propia de cada herramienta. */
export function trazar(ctx: CanvasRenderingContext2D, a: Punto, b: Punto, pincel: Pincel) {
  const { herramienta, color, grosor } = pincel;
  // La presión del dedo o del lápiz modula el grosor: el trazo respira.
  const presion = 0.55 + ((a.presion + b.presion) / 2) * 0.9;
  const ancho = grosor * presion;

  ctx.save();
  ctx.lineCap = "round";
  ctx.lineJoin = "round";

  switch (herramienta) {
    case "borrador":
      ctx.globalCompositeOperation = "destination-out";
      ctx.strokeStyle = "rgba(0,0,0,1)";
      ctx.lineWidth = grosor * 2.4;
      linea(ctx, a, b);
      break;

    case "crayon": {
      // Varias pasadas finas y desalineadas imitan la cera sobre papel.
      ctx.globalAlpha = 0.22;
      ctx.strokeStyle = color;
      const pasadas = Math.max(3, Math.round(ancho / 3));
      for (let i = 0; i < pasadas; i++) {
        ctx.lineWidth = Math.max(1, ancho / 2.2);
        linea(
          ctx,
          { ...a, x: a.x + jitter(ancho * 0.7), y: a.y + jitter(ancho * 0.7) },
          { ...b, x: b.x + jitter(ancho * 0.7), y: b.y + jitter(ancho * 0.7) }
        );
      }
      break;
    }

    case "marcador":
      ctx.globalAlpha = 0.55;
      ctx.strokeStyle = color;
      ctx.lineWidth = ancho * 1.8;
      linea(ctx, a, b);
      break;

    case "neon":
      ctx.shadowColor = color;
      ctx.shadowBlur = ancho * 2.2;
      ctx.strokeStyle = color;
      ctx.lineWidth = ancho * 0.8;
      linea(ctx, a, b);
      ctx.strokeStyle = "#ffffff";
      ctx.globalAlpha = 0.75;
      ctx.lineWidth = Math.max(1, ancho * 0.28);
      linea(ctx, a, b);
      break;

    case "aerosol": {
      ctx.fillStyle = color;
      const radio = ancho * 1.6;
      const gotas = Math.round(radio * 1.6);
      for (let i = 0; i < gotas; i++) {
        const ang = Math.random() * Math.PI * 2;
        const dist = Math.random() * radio;
        ctx.globalAlpha = 0.16 + Math.random() * 0.2;
        ctx.beginPath();
        ctx.arc(b.x + Math.cos(ang) * dist, b.y + Math.sin(ang) * dist, 1.1, 0, Math.PI * 2);
        ctx.fill();
      }
      break;
    }

    default:
      ctx.strokeStyle = color;
      ctx.lineWidth = ancho;
      linea(ctx, a, b);
  }

  ctx.restore();
}

function linea(ctx: CanvasRenderingContext2D, a: Punto, b: Punto) {
  ctx.beginPath();
  ctx.moveTo(a.x, a.y);
  ctx.lineTo(b.x, b.y);
  ctx.stroke();
}

export function sellar(ctx: CanvasRenderingContext2D, p: Punto, emoji: string, tamano: number) {
  ctx.save();
  ctx.font = `${tamano}px serif`;
  ctx.textAlign = "center";
  ctx.textBaseline = "middle";
  ctx.fillText(emoji, p.x, p.y);
  ctx.restore();
}

// ---------------------------------------------------------------------------
// Relleno por regiones (cubeta)
// ---------------------------------------------------------------------------

function hexARgba(hex: string): [number, number, number, number] {
  const limpio = hex.replace("#", "");
  const n = parseInt(limpio.length === 3 ? limpio.replace(/./g, "$&$&") : limpio, 16);
  return [(n >> 16) & 255, (n >> 8) & 255, n & 255, 255];
}

/**
 * Relleno tipo cubeta con tolerancia: pinta la mancha continua que toca el
 * dedo y se detiene en cualquier trazo. Como el lienzo es transparente, el
 * fondo cuenta como una sola región vacía.
 */
export function rellenar(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  color: string,
  tolerancia = 40
) {
  const { width: w, height: h } = ctx.canvas;
  const inicioX = Math.floor(x);
  const inicioY = Math.floor(y);
  if (inicioX < 0 || inicioY < 0 || inicioX >= w || inicioY >= h) return;

  const img = ctx.getImageData(0, 0, w, h);
  const datos = img.data;
  const idx = (px: number, py: number) => (py * w + px) * 4;

  const origen = idx(inicioX, inicioY);
  const base = [datos[origen], datos[origen + 1], datos[origen + 2], datos[origen + 3]];
  const destino = hexARgba(color);

  const igual = (i: number) =>
    Math.abs(datos[i] - base[0]) <= tolerancia &&
    Math.abs(datos[i + 1] - base[1]) <= tolerancia &&
    Math.abs(datos[i + 2] - base[2]) <= tolerancia &&
    Math.abs(datos[i + 3] - base[3]) <= tolerancia;

  if (
    base[3] === destino[3] &&
    base[0] === destino[0] &&
    base[1] === destino[1] &&
    base[2] === destino[2]
  ) {
    return;
  }

  // Relleno por líneas: mucho más rápido que píxel a píxel en pantallas grandes.
  const pila: Array<[number, number]> = [[inicioX, inicioY]];
  while (pila.length > 0) {
    const [px, py] = pila.pop()!;
    let arriba = px;
    while (arriba >= 0 && igual(idx(arriba, py))) arriba--;
    arriba++;
    let abajo = px;
    while (abajo < w && igual(idx(abajo, py))) abajo++;
    abajo--;

    let spanArriba = false;
    let spanAbajo = false;
    for (let i = arriba; i <= abajo; i++) {
      const p = idx(i, py);
      datos[p] = destino[0];
      datos[p + 1] = destino[1];
      datos[p + 2] = destino[2];
      datos[p + 3] = destino[3];

      if (py > 0) {
        const dentro = igual(idx(i, py - 1));
        if (dentro && !spanArriba) {
          pila.push([i, py - 1]);
          spanArriba = true;
        } else if (!dentro) spanArriba = false;
      }
      if (py < h - 1) {
        const dentro = igual(idx(i, py + 1));
        if (dentro && !spanAbajo) {
          pila.push([i, py + 1]);
          spanAbajo = true;
        } else if (!dentro) spanAbajo = false;
      }
    }
  }

  ctx.putImageData(img, 0, 0);
}

// ---------------------------------------------------------------------------
// Exportar y galería
// ---------------------------------------------------------------------------

/** Compone el fondo con el dibujo y devuelve un PNG en dataURL. */
export function exportarPNG(canvas: HTMLCanvasElement, fondo: string): string {
  const salida = document.createElement("canvas");
  salida.width = canvas.width;
  salida.height = canvas.height;
  const ctx = salida.getContext("2d");
  if (!ctx) return canvas.toDataURL("image/png");
  ctx.fillStyle = fondo;
  ctx.fillRect(0, 0, salida.width, salida.height);
  ctx.drawImage(canvas, 0, 0);
  return salida.toDataURL("image/png");
}

/** Versión pequeña para guardar en la galería sin llenar el almacenamiento. */
export function miniatura(canvas: HTMLCanvasElement, fondo: string, ancho = 520): string {
  const escala = ancho / canvas.width;
  const salida = document.createElement("canvas");
  salida.width = ancho;
  salida.height = Math.round(canvas.height * escala);
  const ctx = salida.getContext("2d");
  if (!ctx) return "";
  ctx.fillStyle = fondo;
  ctx.fillRect(0, 0, salida.width, salida.height);
  ctx.drawImage(canvas, 0, 0, salida.width, salida.height);
  return salida.toDataURL("image/jpeg", 0.72);
}

const CLAVE_GALERIA = "pizarra-galeria";
const MAX_DIBUJOS = 12;

export interface DibujoGuardado {
  id: string;
  fecha: number;
  imagen: string;
}

export function leerGaleria(): DibujoGuardado[] {
  if (typeof window === "undefined") return [];
  try {
    return JSON.parse(localStorage.getItem(CLAVE_GALERIA) ?? "[]") as DibujoGuardado[];
  } catch {
    return [];
  }
}

export function guardarEnGaleria(imagen: string): DibujoGuardado[] {
  const dibujo: DibujoGuardado = { id: String(Date.now()), fecha: Date.now(), imagen };
  const lista = [dibujo, ...leerGaleria()].slice(0, MAX_DIBUJOS);
  try {
    localStorage.setItem(CLAVE_GALERIA, JSON.stringify(lista));
  } catch {
    // Si ya no cabe, se conservan solo los más recientes.
    const corta = lista.slice(0, 4);
    localStorage.setItem(CLAVE_GALERIA, JSON.stringify(corta));
    return corta;
  }
  return lista;
}

export function borrarDeGaleria(id: string): DibujoGuardado[] {
  const lista = leerGaleria().filter((d) => d.id !== id);
  localStorage.setItem(CLAVE_GALERIA, JSON.stringify(lista));
  return lista;
}

/**
 * Descarga el dibujo. Dentro del APK el WebView bloquea los enlaces de
 * descarga, así que primero se intenta compartir con la app nativa.
 */
export async function descargar(dataUrl: string, nombre = "mi-dibujo.png") {
  try {
    const blob = await (await fetch(dataUrl)).blob();
    const archivo = new File([blob], nombre, { type: "image/png" });
    const nav = navigator as Navigator & {
      canShare?: (data: { files: File[] }) => boolean;
      share?: (data: { files: File[]; title?: string }) => Promise<void>;
    };
    if (nav.canShare?.({ files: [archivo] }) && nav.share) {
      await nav.share({ files: [archivo], title: "Mi dibujo" });
      return true;
    }
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = nombre;
    a.click();
    setTimeout(() => URL.revokeObjectURL(url), 4000);
    return true;
  } catch {
    return false;
  }
}
