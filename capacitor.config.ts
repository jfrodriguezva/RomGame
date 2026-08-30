import type { CapacitorConfig } from "@capacitor/cli";

const config: CapacitorConfig = {
  appId: "com.romgame.app",
  appName: "Mi Ambiente",
  webDir: "out",
  android: {
    // El fondo del WebView debe coincidir con el de la app para que no se vea
    // un destello blanco al abrir.
    backgroundColor: "#fdfaf5",
  },
  plugins: {
    SplashScreen: {
      launchAutoHide: true,
      launchShowDuration: 700,
      backgroundColor: "#fdfaf5",
    },
  },
};

export default config;
