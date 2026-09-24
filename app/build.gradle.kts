import java.util.Properties

val gdxNatives by configurations.creating

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Firma de release: keystore.properties es un secreto local (gitignored,
// ver keystore.properties.example). Si no existe todavía — por ejemplo en
// un clon nuevo del repo — el build de release simplemente queda sin
// firmar en vez de fallar, así assembleDebug/compileDebugKotlin nunca se
// rompen por esto.
val keystoreProperties = Properties().apply {
    val archivo = rootProject.file("keystore.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

android {
    namespace = "com.miambiente.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.miambiente.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        androidResources {
            ignoreAssetsPattern = "!.svn:!.git:!.gitignore:!.ds_store:!*.scc:<dir>_*:!CVS:!thumbs.db:!picasa.ini:!*~"
        }
    }

    signingConfigs {
        if (keystoreProperties.containsKey("storeFile")) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (keystoreProperties.containsKey("storeFile")) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    sourceSets {
        getByName("main") {
            kotlin.srcDirs("src/main/kotlin")
            jniLibs.srcDir(layout.buildDirectory.dir("generated/gdx-natives"))
        }
        getByName("test") {
            kotlin.srcDirs("src/test/kotlin")
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    val gdxVersion = "1.14.2"
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.fragment:fragment-ktx:1.8.4")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.animation:animation")

    implementation("androidx.navigation:navigation-compose:2.8.2")

    implementation(project(":game-core"))
    implementation("org.godotengine:godot:4.7.2.stable")
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    gdxNatives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    gdxNatives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    gdxNatives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    gdxNatives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
    gdxNatives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-armeabi-v7a")
    gdxNatives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-arm64-v8a")
    gdxNatives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86")
    gdxNatives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86_64")

    // Persistencia — equivalente nativo de zustand+localStorage (settings.ts, progressStore.ts).
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    debugImplementation("androidx.compose.ui:ui-tooling")

    // Pruebas internas de la lógica pura (curvas de nivel, detección de
    // ganador de los juegos de mesa, generación del set de dominó) — JVM
    // puro, sin emulador: corren en segundos con `./gradlew testDebugUnitTest`.
    testImplementation("junit:junit:4.13.2")
}

val copyGdxNatives by tasks.registering {
    val output = layout.buildDirectory.dir("generated/gdx-natives")
    outputs.dir(output)
    doLast {
        delete(output)
        gdxNatives.files.forEach { archive ->
            val abi = when {
                "arm64-v8a" in archive.name -> "arm64-v8a"
                "armeabi-v7a" in archive.name -> "armeabi-v7a"
                "x86_64" in archive.name -> "x86_64"
                else -> "x86"
            }
            copy {
                from(zipTree(archive)) { include("*.so") }
                into(output.get().dir(abi))
            }
        }
    }
}

tasks.matching { it.name.endsWith("JniLibFolders") }.configureEach {
    dependsOn(copyGdxNatives)
}
