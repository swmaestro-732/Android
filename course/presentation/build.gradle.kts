import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.chillsam.courmy.course.presentation"
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
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
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

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
    implementation(project(":course:domain"))
    implementation(project(":course:entity"))
    implementation(project(":common:presentation"))

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // 사진 담기: 시스템 Photo Picker(activity-compose) + 선택 이미지 렌더(Coil)
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil.compose)

    // 네이버 지도(코스 경로 표시). map-sdk 는 transitive 로 함께 온다.
    implementation(libs.naver.map.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // 코스 상세 안의 장소 상세 시트가 hiltViewModel() 로 ViewModel 을 얻는다.
    implementation(libs.androidx.hilt.navigation.compose)

    // @Preview 애노테이션(main 소스에서 사용) + 렌더러(debug 전용)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
