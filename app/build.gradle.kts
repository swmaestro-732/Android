import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.baselineprofile)
}

// 릴리스 서명 정보 — 로컬 keystore.properties 또는 CI 환경변수(GitHub Secrets)에서 읽는다.
// 값이 없으면 debug 서명으로 폴백한다(초기 단계: keystore 없이도 아티팩트 빌드는 되게,
// secret 을 등록하면 자동으로 실서명으로 승격). keystore.properties 는 .gitignore 처리.
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps =
    Properties().apply {
        if (keystorePropsFile.exists()) keystorePropsFile.inputStream().use { load(it) }
    }

fun signingSecret(
    propKey: String,
    envKey: String,
): String? = (keystoreProps.getProperty(propKey) ?: System.getenv(envKey))?.takeIf { it.isNotBlank() }

val releaseStoreFile = signingSecret("storeFile", "KEYSTORE_FILE")
val hasReleaseSigning = releaseStoreFile != null

// 네이버 지도 인증(Client ID). local.properties(NAVER_MAP_CLIENT_ID) 또는 CI 환경변수에서 읽는다.
// 값이 없으면 빈 문자열로 빌드는 되게 두고(초기 단계), 값이 들어오면 지도가 인증된다.
val localProps =
    Properties().apply {
        val f = rootProject.file("local.properties")
        if (f.exists()) f.inputStream().use { load(it) }
    }
val naverMapClientId: String =
    (localProps.getProperty("NAVER_MAP_CLIENT_ID") ?: System.getenv("NAVER_MAP_CLIENT_ID")).orEmpty()

android {
    namespace = "com.chillsam.courmy"
    compileSdk {
        version =
            release(
                libs.versions.compileSdk
                    .get()
                    .toInt(),
            )
    }

    defaultConfig {
        applicationId = "com.chillsam.courmy"
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // AndroidManifest 의 네이버 지도 meta-data(${naverMapClientId})로 주입된다.
        manifestPlaceholders["naverMapClientId"] = naverMapClientId
    }

    signingConfigs {
        // secret/keystore.properties 가 있을 때만 release 서명 구성을 만든다.
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStoreFile!!)
                storePassword = signingSecret("storePassword", "KEYSTORE_PASSWORD")
                keyAlias = signingSecret("keyAlias", "KEY_ALIAS")
                keyPassword = signingSecret("keyPassword", "KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // keystore 준비되면 release 서명, 없으면 debug 폴백(초기 아티팩트 빌드용)
            signingConfig =
                if (hasReleaseSigning) {
                    signingConfigs.getByName("release")
                } else {
                    signingConfigs.getByName("debug")
                }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        // Macrobenchmark / Baseline Profile 수집 시 사용되는 빌드 타입.
        // release 와 동일한 최적화 상태를 가지면서, ART 가 메서드 trace 를 dump 할 수 있도록
        // profileable 로 표시한다. androidx.baselineprofile 플러그인이 자동으로 생성/사용한다.
        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            isProfileable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

// Compose Compiler reports/metrics — enable with `-Pcomposecompiler.reports=true`.
composeCompiler {
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("compose_stability.conf"))
    if (providers.gradleProperty("composecompiler.reports").orNull == "true") {
        val outDir =
            rootProject.layout.buildDirectory.dir(
                "compose_reports/${project.path.replace(":", "_").trim('_')}",
            )
        reportsDestination.set(outDir)
        metricsDestination.set(outDir)
    }
}

dependencies {
    implementation(project(":common:presentation"))
    implementation(project(":common:domain"))
    implementation(project(":common:data"))
    implementation(project(":common:entity"))

    implementation(project(":main:presentation"))
    implementation(project(":main:domain"))
    implementation(project(":main:data"))
    implementation(project(":main:entity"))

    implementation(project(":course:presentation"))
    implementation(project(":course:domain"))
    implementation(project(":course:data"))
    implementation(project(":course:entity"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Baseline Profile: 설치 시점에 dump 된 프로필을 ART 에 등록해 주는 런타임 라이브러리.
    // minSdk 24 ~ 27 백포트를 위해 필수.
    implementation(libs.androidx.profileinstaller)

    // 빌드 시 :baselineprofile 모듈이 만들어 둔 baseline-prof.txt 를 가져다
    // 자동으로 src/main/baseline-prof.txt 위치로 머지/패키징 한다.
    "baselineProfile"(project(":baselineprofile"))
}
