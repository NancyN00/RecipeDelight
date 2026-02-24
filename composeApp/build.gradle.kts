import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
val gKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("RecipeDatabase") {
            packageName.set("com.nancy.recipedelight.data.local")
        }
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.androidx.compose.material.icons.core)
                implementation(libs.androidx.compose.material.icons.extended)

                // Ktor Android engine + Koin Android
                implementation(libs.ktor.client.okhttp)
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)


                //sqldelight
                implementation(libs.sqldelight.android.driver)

                //splash
                implementation(libs.androidx.core.splashscreen)

                //coil
                implementation(libs.coil.compose)

                implementation(libs.retrofit)
                implementation(libs.converter.gson)
                implementation(libs.okhttp)
                implementation(libs.logging.interceptor)

            }
        }
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Lifecycle
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                // Serialization
                implementation(libs.kotlinx.serialization.json)

                // Koin
                implementation(libs.koin.core)

                //SqlDelight
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.primitive.adapters)


            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "com.nancy.recipedelight"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.nancy.recipedelight"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        buildConfig = true
    }


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "GEMINI_API_KEY", "\"$gKey\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "GEMINI_API_KEY", "\"$gKey\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
