plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Nuevo plugin obligatorio para Kotlin 2.0+ que reemplaza composeOptions
    alias(libs.plugins.kotlin.compose)
    // Habilita Kapt para el procesamiento de la base de datos Room
    id("kotlin-kapt")
}

android {
    // Identificador único de tu aplicación
    namespace = "com.example.pda"
    compileSdk = 35 // Actualizado a 35 para compatibilidad con librerías modernas

    defaultConfig {
        applicationId = "com.example.pda"
        minSdk = 24 // Compatible con Android 7.0 o superior
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // Silencia avisos de APIs experimentales en todo el proyecto
        freeCompilerArgs = freeCompilerArgs + "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    }

    buildFeatures {
        // Habilita el soporte para Jetpack Compose
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Librerías base de Android y Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    // Componentes de UI Material 3
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Iconos extendidos para el selector de bus (DirectionsBus)
    implementation(libs.androidx.material.icons.extended)

    // Room - Base de Datos Local
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


}