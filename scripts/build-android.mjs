#!/usr/bin/env node
/**
 * Construye el APK de Android en un solo comando y en cualquier sistema.
 *
 *   npm run export    solo genera el sitio estático en out/
 *   npm run android   además sincroniza Capacitor y compila el APK de debug
 *
 * Se hace en Node en vez de en la línea de comandos porque `CAP_BUILD=1 next
 * build` no funciona igual en Windows que en Linux, y el proyecto se compila
 * en las dos partes: aquí y en GitHub Actions.
 */

import { spawnSync } from "node:child_process";
import { existsSync } from "node:fs";
import { join } from "node:path";
import { platform } from "node:process";

const soloWeb = process.argv.includes("--solo-web");
const raiz = process.cwd();
const esWindows = platform === "win32";

function ejecutar(comando, args, opciones = {}) {
  console.log(`\n▸ ${comando} ${args.join(" ")}`);
  const res = spawnSync(comando, args, {
    stdio: "inherit",
    shell: esWindows,
    ...opciones,
  });
  if (res.status !== 0) {
    console.error(`\n✗ Falló: ${comando} ${args.join(" ")}`);
    process.exit(res.status ?? 1);
  }
}

// 1. Export estático. CAP_BUILD hace que next.config.ts genere HTML puro.
ejecutar("npx", ["next", "build"], { env: { ...process.env, CAP_BUILD: "1" } });

if (soloWeb) {
  console.log("\n✓ Sitio estático listo en out/");
  process.exit(0);
}

// 2. Copiar el sitio dentro del proyecto Android.
ejecutar("npx", ["cap", "sync", "android"]);

// 3. Compilar el APK.
const gradlew = join(raiz, "android", esWindows ? "gradlew.bat" : "gradlew");
if (!existsSync(gradlew)) {
  console.error("\n✗ No se encontró android/gradlew. ¿Está el proyecto Android?");
  process.exit(1);
}

ejecutar(gradlew, ["assembleDebug", "--no-daemon"], { cwd: join(raiz, "android") });

console.log(
  "\n✓ APK listo en android/app/build/outputs/apk/debug/app-debug.apk" +
    "\n  Cópialo al teléfono e instálalo, o usa: adb install -r <ruta>"
);
