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

        // API-CONFIG-INJECTION-POINT: 베이스 URL 은 local.properties 또는 환경변수로 주입한다.
        //   API_BASE_URL=...    (미설정 시 Courmy 운영 백엔드로 폴백)
        // 인증은 JWT accessToken(Bearer)만 쓰며, 공개 엔드포인트는 인증이 필요 없다(별도 API key 없음).
        //
        // CI 러너에는 local.properties 가 없으므로 환경변수 폴백이 필요하다. 미등록 secret 은
        // 빈 문자열로 들어오기 때문에 null 이 아니라 blank 로 판정해야 폴백이 걸린다.
        val localProps =
            Properties().apply {
                val f = rootProject.file("local.properties")
                if (f.exists()) f.inputStream().use { load(it) }
            }
        val apiBaseUrl =
            listOf(localProps.getProperty("API_BASE_URL"), System.getenv("API_BASE_URL"))
                .firstOrNull { !it.isNullOrBlank() }
                ?: "https://courmy.com/"
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

    // Telemetry 구현 — 크래시(non-fatal 포함)와 핵심 플로우 이벤트.
    // google-services.json 은 app 모듈에만 필요하고, 라이브러리는 SDK 만 있으면 된다.
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
