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
      }
    : {}),
};

export default nextConfig;
