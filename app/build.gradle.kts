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

// 카카오 로그인 네이티브 앱키. KakaoSdk.init 과 카카오톡 로그인 리다이렉트 scheme(kakao{appKey})에 쓰인다.
val kakaoNativeAppKey: String =
    (localProps.getProperty("KAKAO_NATIVE_APP_KEY") ?: System.getenv("KAKAO_NATIVE_APP_KEY")).orEmpty()

// Firebase Crashlytics — google-services.json 이 있을 때만 플러그인을 붙인다.
// 이 파일이 없는데 플러그인을 적용하면 빌드가 통째로 깨지므로, keystore·카카오 키와 같은 방식으로
// "없으면 크래시 리포팅만 빠지고 빌드는 진행" 하게 둔다(릴리스에서는 아래에서 하드 실패).
val hasFirebaseConfig = file("google-services.json").exists()
if (hasFirebaseConfig) {
    apply(
        plugin =
            libs.plugins.google.services
                .get()
                .pluginId,
    )
    apply(
        plugin =
            libs.plugins.firebase.crashlytics
                .get()
                .pluginId,
    )
}

// 릴리스 빌드는 앱키가 비면 KakaoSdk.init 이 스킵되고 리다이렉트 scheme 가 깨지므로, 패키징 전에 즉시 실패시킨다.
// (디버그/로컬 개발은 키 없이도 진행 가능하게 둔다.)
gradle.taskGraph.whenReady {
    val buildingRelease = allTasks.any { it.name.contains("Release", ignoreCase = true) }
    if (buildingRelease && kakaoNativeAppKey.isBlank()) {
        throw GradleException("KAKAO_NATIVE_APP_KEY 가 설정되지 않았습니다. 릴리스 빌드에는 필수입니다(local.properties 또는 환경변수).")
    }
    // 크래시 리포팅 없이 출시하면 운영 중 장애를 볼 수단이 없으므로 릴리스에서는 필수로 둔다.
    if (buildingRelease && !hasFirebaseConfig) {
        throw GradleException("google-services.json 이 없습니다. 릴리스 빌드에는 필수입니다(Crashlytics).")
    }

    // 서명 설정이 비면 아래 buildTypes 의 debug 폴백이 걸려, bundleRelease 가 "성공" 하면서
    // debug 서명 AAB 를 뱉는다. Play 업로드 단계에서야 거부당해 원인을 찾기 어려우므로 여기서 끊는다.
    // 위 두 가드와 달리 배포 산출물을 만드는 task 로만 좁힌다 — benchmark/nonMinifiedRelease 변형은
    // 이름에 Release 가 붙지만 의도적으로 debug 서명이라, 업로드 키가 없다고 막으면 안 된다.
    val packagingRelease = allTasks.any { it.name == "packageRelease" || it.name == "bundleRelease" }
    if (packagingRelease) {
        val missingSigning =
            listOf(
                "storeFile" to releaseStoreFile,
                "storePassword" to signingSecret("storePassword", "KEYSTORE_PASSWORD"),
                "keyAlias" to signingSecret("keyAlias", "KEY_ALIAS"),
                "keyPassword" to signingSecret("keyPassword", "KEY_PASSWORD"),
            ).filter { it.second == null }
                .map { it.first }
        if (missingSigning.isNotEmpty()) {
            throw GradleException(
                "릴리스 서명 설정이 비었습니다: ${missingSigning.joinToString()}. " +
                    "keystore.properties 또는 KEYSTORE_FILE/KEYSTORE_PASSWORD/KEY_ALIAS/KEY_PASSWORD 환경변수를 채우세요.",
            )
        }
        if (!file(releaseStoreFile!!).exists()) {
            throw GradleException("keystore 파일이 없습니다: $releaseStoreFile")
        }
    }
}

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
        // Play 는 versionCode 로만 버전 순서를 판단한다. 업로드할 때마다 반드시 올려야 하고,
        // 한 번 쓴 값은 재사용할 수 없다(같은 값으로 올리면 중복으로 거부된다).
        versionCode = 1
        // 사용자에게 보이는 표시용 문자열. 정식 출시 전이라 0.x 로 둔다.
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // AndroidManifest 의 네이버 지도 meta-data(${naverMapClientId})로 주입된다.
        manifestPlaceholders["naverMapClientId"] = naverMapClientId

        // 카카오: KakaoSdk.init 용 BuildConfig + 카카오톡 로그인 리다이렉트 scheme(manifest) 주입.
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoNativeAppKey\"")
        manifestPlaceholders["kakaoNativeAppKey"] = kakaoNativeAppKey
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
            // Play Console 의 "디버그 기호가 업로드되지 않았습니다" 경고 대응.
            //
            // 다만 이 설정만으로는 경고가 사라지지 않는다. 확인해 보면 extractReleaseNativeSymbolTables
            // 가 .so 24개를 입력받아 0개를 내놓는다 — 우리 네이티브 코드는 없고, 들어 있는 .so 는 전부
            // 서드파티(libnavermap.so 등)인데 벤더가 이미 stripped 로 배포해 추출할 심볼이 없다.
            // 네이버가 unstripped 로 배포하지 않는 한 이 경고는 남는다.
            //
            // 그럼에도 켜 두는 건, 나중에 프로젝트에 네이티브 코드가 생기거나 의존성이 심볼을 달고
            // 오면 자동으로 담기게 하기 위함이다. 추출물이 없으니 빌드 시간·용량 비용은 0 이다.
            ndk { debugSymbolLevel = "SYMBOL_TABLE" }
            // keystore 준비되면 release 서명. 폴백은 서명 없이도 configuration 이 통과하게 하려는 것뿐이고,
            // 실제로 debug 서명 산출물이 나가는 건 위 taskGraph 가드가 막는다.
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
            // R8 매핑을 올려야 Crashlytics 가 난독화된 스택을 원래 이름으로 되돌린다.
            // 이게 빠지면 리포트가 a.b.c() 로 보여 사실상 쓸모가 없다. (기본값도 true 지만 명시해 둔다)
            if (hasFirebaseConfig) {
                configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                    mappingFileUploadEnabled = true
                }
            }
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
            // release 를 그대로 물려받아 minify 가 켜져 있어, 두지 않으면 프로필 수집 때마다
            // 매핑 업로드가 따라붙는다. 배포되지 않는 빌드라 끈다.
            if (hasFirebaseConfig) {
                configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                    mappingFileUploadEnabled = false
                }
            }
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

    // 카카오 로그인 SDK — CourmyApplication 의 KakaoSdk.init 용
    implementation(libs.kakao.user)

    // Firebase — 출시 후 크래시·ANR 리포팅. 개별 라이브러리 버전은 BOM 이 맞춘다.
    // Crashlytics 는 코드 없이 크래시를 잡고, Analytics 는 리포트에 breadcrumb 를 붙여 준다.
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // Baseline Profile: 설치 시점에 dump 된 프로필을 ART 에 등록해 주는 런타임 라이브러리.
    // minSdk 24 ~ 27 백포트를 위해 필수.
    implementation(libs.androidx.profileinstaller)

    // 빌드 시 :baselineprofile 모듈이 만들어 둔 baseline-prof.txt 를 가져다
    // 자동으로 src/main/baseline-prof.txt 위치로 머지/패키징 한다.
    "baselineProfile"(project(":baselineprofile"))
}
