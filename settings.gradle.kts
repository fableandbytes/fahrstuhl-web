pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    // PREFER_PROJECT ist wichtig, damit das Kotlin-Plugin für WasmJS interne Repos (wie für Binaryen) hinzufügen kann
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev") }
        maven { url = uri("https://androidx.dev/storage/compose-compiler/repository") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        // Dieses Repo enthält oft die benötigten Wasm-Tools
        maven { url = uri("https://packages.jetbrains.team/maven/p/firework/dev") }
    }
}

rootProject.name = "FahrstuhlKartenspiel"
include(":app")
