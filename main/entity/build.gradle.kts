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
    // 커서 페이징 VO(CursorPageVO)를 공개 시그니처에 쓰므로 api 로 노출한다.
    api(project(":common:entity"))
    api(libs.kotlinx.serialization.json)
}
