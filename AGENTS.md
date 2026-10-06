# RominaGame agent rules

- This repository is an Android application written in Kotlin and Jetpack Compose.
- Use JDK 21 to run Gradle; Android source compatibility remains Java 17.
- Run `gradlew testDebugUnitTest lintDebug assembleDebug` after functional changes.
- With an emulator or device attached, also run `gradlew connectedDebugAndroidTest`: it opens every screen and enforces 48dp touch targets.
- Keep game rules independent from rendering whenever practical so they can be unit tested.
- The long-term architecture is documented in `docs/PLAN-RECONSTRUCCION-ANDROID.md`.
- React, Next.js, Capacitor and WebView are not part of the product and must not be reintroduced.
- `web/` is the browser build (Kotlin/Wasm + Compose Multiplatform). It compiles the same `model/`, `theme/` and `ui/` folders of `:app` without copying them; browser replacements for Android services live only in `web/` (`data/web`, `ui/web`), and `web/.../compat` rebuilds on Skia the subset of `android.graphics`/`androidx.core.graphics` the pizarra uses; extend it there if the pizarra starts using more. Never change `:app` code just to please the web build: shared code must keep compiling for both (CI runs `:web:compileKotlinWasmJs`). Locally, behind the office proxy, set `ROMINA_NODE_LOCAL=1` to use the installed Node.
- `pagina/` is served at `/emulador/`: the real APK on an Appetize.io Android emulator, for the final check before release.
