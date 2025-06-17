pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
    // Adicionar estratégia de resolução para forçar atualização das dependências do Supabase
    versionCatalogs {
        create("libs") {
            version("supabaseBom", "1.4.8") // Atualizado para 1.4.8
        }
    }
}

rootProject.name = "Alquimia"
include(":app")
