// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
    // Mover a declaração do plugin google-services para o bloco plugins aqui
    id("com.google.gms.google-services") version "4.4.0" apply false
}
