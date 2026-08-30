import type { NextConfig } from "next";

// CAP_BUILD=1 genera un export estático (HTML/JS/CSS puro) para empaquetar
// con Capacitor en el APK de Android. El resto del tiempo (npm run dev / start)
// sigue funcionando normal como servidor Next.js.
const nextConfig: NextConfig = {
  ...(process.env.CAP_BUILD
    ? {
        output: "export",
        distDir: "out",
        images: { unoptimized: true },
        // Cada ruta se emite como carpeta con index.html. El servidor local de
        // Capacitor resuelve directorios, pero no adivina la extension .html:
        // sin esto, recargar dentro de un juego daria 404 en el APK.
        trailingSlash: true,
      }
    : {}),
};

export default nextConfig;
