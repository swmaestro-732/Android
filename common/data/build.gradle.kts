import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.chillsam.courmy.common.data"
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()

        // API-CONFIG-INJECTION-POINT: API 키/베이스 URL 은 local.properties 에서 주입한다.
        //   API_KEY=...         (gitignore 대상 — 소스에 하드코딩 금지. QA/Prod 는 CI 단계에서 주입)
        //   API_BASE_URL=...    (미설정 시 레퍼런스 feature(search)용 카카오 OpenAPI 로 폴백)
        val localProps =
            Properties().apply {
                val f = rootProject.file("local.properties")
                if (f.exists()) f.inputStream().use { load(it) }
            }
        val apiKey = localProps.getProperty("API_KEY") ?: ""
        val apiBaseUrl =
            localProps.getProperty("API_BASE_URL") ?: "https://d2ovt2o1pkjfc.cloudfront.net/"
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

dependencies {
    implementation(project(":common:domain"))
    implementation(project(":common:entity"))

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // 세션 토큰 암호화 영속화(Keystore 암호문 → DataStore).
    implementation(libs.androidx.datastore.preferences)
}
