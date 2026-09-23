# RominaGame agent rules

- This repository is an Android application written in Kotlin and Jetpack Compose.
- Use JDK 21 to run Gradle; Android source compatibility remains Java 17.
- Run `gradlew :game-core:test :app:testDebugUnitTest lintDebug assembleDebug bundleRelease` after functional changes.
- Regenerate compiled Godot resources with `tools/export-godot.ps1` after changing GDScript.
- Keep game rules independent from rendering whenever practical so they can be unit tested.
- The long-term architecture is documented in `docs/PLAN-RECONSTRUCCION-ANDROID.md`.
- React, Next.js, Capacitor and WebView are not part of the product and must not be reintroduced.
