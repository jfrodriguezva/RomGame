# Instalar «Mi Ambiente» en una tablet Android

El archivo que necesitas es **`app-debug.apk`**. Se genera en:

```
android/app/build/outputs/apk/debug/app-debug.apk
```

Requiere **Android 7.0 o superior** (minSdk 24).

Sobre permisos: el APK declara `INTERNET`, que es el permiso que Capacitor
incluye por omisión para su WebView. La app **no hace ninguna petición de red**:
todo el contenido viaja dentro del APK y el progreso se guarda en el
almacenamiento local. Puedes comprobarlo usándola en modo avión: funciona
igual. No pide cámara, micrófono, ubicación, contactos ni almacenamiento.

---

## Opción A — Pasarlo por cable o por la nube (lo más simple)

1. Copia `app-debug.apk` a la tablet: por cable USB, por Google Drive, por
   WhatsApp a ti mismo, por correo… da igual el medio.
2. En la tablet, abre el archivo con el **explorador de archivos** (Archivos,
   Files, Mis archivos…). No lo abras desde la app de descargas si te da
   problemas: busca el APK en la carpeta *Descargas*.
3. Android te dirá que esa app no tiene permiso para instalar aplicaciones
   desconocidas. Toca **Configuración** → activa **Permitir de esta fuente** →
   vuelve atrás.
4. Toca **Instalar**. Si aparece Play Protect diciendo que no reconoce la app,
   elige **Instalar de todos modos** (es normal: el APK no viene de la tienda).
5. Listo. Aparece en el cajón de apps como **Mi Ambiente** 🦉.

---

## Opción B — Por cable con `adb`

Con la tablet conectada por USB y la **depuración USB** activada
(Ajustes → Acerca de la tablet → toca 7 veces «Número de compilación» →
Ajustes → Opciones de desarrollador → Depuración por USB):

```bash
adb install -r android/app/build/outputs/apk/debug/app-debug.apk
```

`adb` está en `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe`.

---

## Volver a compilar después de cambiar algo

```bash
npm run android
```

Necesitas las variables de entorno del JDK y del SDK. Si abres una terminal
nueva y el comando falla, es porque no están puestas:

```powershell
$env:JAVA_HOME = "$env:LOCALAPPDATA\Programs\jdk21"
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

Para no repetirlo cada vez, déjalas fijas de una sola vez:

```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "$env:LOCALAPPDATA\Programs\jdk21", "User")
[Environment]::SetEnvironmentVariable("ANDROID_HOME", "$env:LOCALAPPDATA\Android\Sdk", "User")
```

---

## Ajustes recomendados en la tablet

Antes de dársela al niño, en **Ajustes de Android**:

- **Fijar la pantalla** (Seguridad → Fijar apps): mantiene la app abierta y evita
  que salga a otras aplicaciones con un gesto.
- **Brillo automático apagado** y brillo medio.
- **Volumen a la mitad**: los sonidos son suaves, no hace falta más.

Y dentro de la app, en **Mamá y papá**:

- Escribe su nombre para que lo salude al entrar.
- **Modo calma** si se distrae con el fondo o el confeti.
- Apaga la **voz** si la tablet no tiene voz en español instalada (se oiría en
  inglés). Se instala en Ajustes → Sistema → Idiomas → Salida de texto a voz.

---

## Notas

- El APK es de **depuración** y está firmado con la clave de depuración de
  Android. Sirve para instalarlo en tus propios dispositivos; **no** sirve para
  publicarlo en Google Play, que exige una firma de release propia.
- El progreso vive en el almacenamiento local de la app. Si desinstalas la app,
  se borra. No hay copia en la nube a propósito.
