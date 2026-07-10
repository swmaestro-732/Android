// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.kover)
}

// Kover — 도메인 모듈 유닛테스트 커버리지 집계 (게이트 없음, 측정·리포트만).
// 집계 리포트: ./gradlew koverXmlReport / koverHtmlReport
dependencies {
    kover(project(":common:domain"))
    kover(project(":main:domain"))
}
