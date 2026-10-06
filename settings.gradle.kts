pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    // PREFER_PROJECT: el plugin de Kotlin para web agrega sus propios
    // repositorios (Node.js, Binaryen) al proyecto raíz para descargar las
    // herramientas de compilación. :app no declara ninguno, así que sigue
    // usando solo los de aquí.
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MiAmbienteNativo"
include(":app")
// Versión para navegador: reutiliza las pantallas de :app sin modificarlas.
include(":web")
