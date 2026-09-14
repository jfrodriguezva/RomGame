import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { MotionConfig } from "framer-motion";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Mi Ambiente — juegos Montessori",
  description:
    "Ambiente Montessori digital para niños de 3 a 6 años: 91 materiales con 100 niveles cada uno y una pizarra de dibujo libre.",
  manifest: "/manifest.json",
  appleWebApp: { capable: true, title: "Mi Ambiente", statusBarStyle: "default" },
};

export const viewport = {
  width: "device-width",
  initialScale: 1,
  maximumScale: 1,
  userScalable: false,
  viewportFit: "cover" as const,
  themeColor: "#fdfaf5",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="es"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="flex min-h-full select-none flex-col bg-[#fdfaf5]">
        {/* Respeta "reducir movimiento" del sistema: cambia las animaciones
            de framer-motion por su transición mas corta en toda la app,
            una sola vez, sin tocar cada material. */}
        <MotionConfig reducedMotion="user">{children}</MotionConfig>
      </body>
    </html>
  );
}
