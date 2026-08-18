plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

dependencies {
    // 커서 페이징 VO(CursorPageVO) 공용.
    api(project(":common:entity"))
    api(libs.kotlinx.serialization.json)
}
