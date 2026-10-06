import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

/**
 * RominaGame para navegador (Kotlin/Wasm + Compose Multiplatform).
 *
 * No copia ni modifica el código del APK: compila las mismas carpetas
 * model/, theme/ y ui/ de :app, y aporta aquí solo lo que en Android usa
 * APIs del sistema (voz, sonido, vibración, guardado). El APK se sigue
 * construyendo igual desde :app.
 */
val compartido = rootProject.file("app/src/main/kotlin")

compose.resources {
    // En un paquete que el filtro de fuentes de abajo deja pasar.
    packageOfResClass = "com.miambiente.app.web.recursos"
    publicResClass = false
    generateResClass = org.jetbrains.compose.resources.ResourcesExtension.ResourceClassGeneration.Always
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("romina")
        browser {
            commonWebpackConfig { outputFileName = "romina.js" }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:1.12.1")
            implementation("org.jetbrains.compose.foundation:foundation:1.12.1")
            implementation("org.jetbrains.compose.ui:ui:1.12.1")
            implementation("org.jetbrains.compose.animation:animation:1.12.1")
            implementation("org.jetbrains.compose.material3:material3:1.9.0")
            implementation("org.jetbrains.compose.components:components-resources:1.12.1")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.2")
        }
        wasmJsMain.configure {
            kotlin.srcDir(compartido)
            kotlin.include(
                "com/miambiente/app/model/**",
                "com/miambiente/app/theme/**",
                "com/miambiente/app/ui/**",
                "com/miambiente/app/data/ResumenAdulto.kt",
                "com/miambiente/app/data/Guias.kt",
                // Lo propio de la web (vive en web/src/wasmJsMain/kotlin).
                "com/miambiente/app/web/**",
                "com/miambiente/app/data/web/**",
                "com/miambiente/app/ui/web/**",
            )
            // La pizarra dibuja con Bitmap/Canvas de Android: en la web, por
            // ahora, se reemplaza por ui/web/PizarraWeb.kt.
            kotlin.exclude("**/ui/screens/PizarraScreen.kt", "**/ui/materials/PizarraMotor.kt")
        }
    }
}
