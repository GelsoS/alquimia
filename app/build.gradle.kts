import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.alquimia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.alquimia"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Ler variáveis do local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        // Remover variáveis do Supabase
        // buildConfigField("String", "SUPABASE_URL", "\"${localProperties.getProperty("SUPABASE_URL") ?: ""}\"")
        // buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProperties.getProperty("SUPABASE_ANON_KEY") ?: ""}\"")

        // Adicionar variável para o URL base do seu backend Node.js
        buildConfigField("String", "BACKEND_BASE_URL", "\"${localProperties.getProperty("BACKEND_BASE_URL") ?: "http://10.0.2.2:3000"}\"") // 10.0.2.2 é o localhost do emulador Android

        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${localProperties.getProperty("GOOGLE_WEB_CLIENT_ID") ?: ""}\"")
        buildConfigField("String", "FACEBOOK_APP_ID", "\"${localProperties.getProperty("FACEBOOK_APP_ID") ?: ""}\"")
        buildConfigField("String", "FACEBOOK_CLIENT_TOKEN", "\"${localProperties.getProperty("FACEBOOK_CLIENT_TOKEN") ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Hilt - versões estáveis
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Serialization (manter para modelos de dados)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // Location services
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Google Sign-In - versões mais estáveis compatíveis com Android 34
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.credentials:credentials:1.2.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    // REMOVER Supabase dependencies
    // implementation("io.github.jan-tennert.supabase:supabase-kt:1.4.7")
    // implementation("io.github.jan-tennert.supabase:postgrest-kt:1.4.7")
    // implementation("io.github.jan-tennert.supabase:storage-kt:1.4.7")
    // implementation("io.github.jan-tennert.supabase:realtime-kt:1.4.7")
    // implementation("io.github.jan-tennert.supabase:gotrue-kt-android:1.4.8")

    // REMOVER Ktor Clients
    // implementation("io.ktor:ktor-client-core:2.3.4")
    // implementation("io.ktor:ktor-client-android:2.3.4")

    // Facebook Login SDK
    implementation("com.facebook.android:facebook-login:17.0.0")

    // Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Retrofit and GSON <--- ADICIONAR ESTAS DEPENDÊNCIAS
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Para logs de rede

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
