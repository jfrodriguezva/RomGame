plugins {
    id("com.android.application") version "9.4.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20" apply false
    // Solo para el módulo :web (la app para navegador); :app no los usa.
    id("org.jetbrains.kotlin.multiplatform") version "2.4.20" apply false
    id("org.jetbrains.compose") version "1.12.1" apply false
}

// En redes que bloquean nodejs.org (la de la oficina responde 403), la
// versión web se compila con el Node instalado en la máquina en lugar de
// descargarlo: basta con definir ROMINA_NODE_LOCAL=1. La CI lo descarga.
if (providers.environmentVariable("ROMINA_NODE_LOCAL").isPresent) {
    allprojects {
        plugins.withType<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsPlugin> {
            the<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec>().download.set(false)
        }
    }
}
