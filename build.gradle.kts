// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false // Adicione a versão aqui
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Certifique-se de que o classpath do Safe Args está aqui
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}
