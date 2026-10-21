// Top-level build file where you can add configuration options common to all sub-projects/modules.
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

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
    alias(libs.plugins.detekt) apply false
    // Firebase — app 모듈이 google-services.json 이 있을 때만 조건부로 적용한다.
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

// Kover — 도메인 모듈 유닛테스트 커버리지 집계.
// 집계 리포트: ./gradlew koverXmlReport / koverHtmlReport, 게이트: ./gradlew koverVerify
dependencies {
    kover(project(":common:domain"))
    kover(project(":main:domain"))
}

// 커버리지 게이트 — 집계 LINE 커버리지 하한. 회귀 방지용 ratchet 바닥값이며,
// 테스트가 쌓이면 이 숫자를 올린다(현재 도메인 LINE ≈ 41%, 초기 버퍼로 30% 설정).
kover {
    reports {
        verify {
            rule {
                minBound(30) // 기본 지표 = LINE, COVERED_PERCENTAGE
            }
        }
    }
}

// Detekt — 코드 스멜/복잡도/잠재버그 정적분석. 포맷(ktlint)과 역할 분리.
// 전 서브모듈에 동일 규칙(config/detekt/detekt.yml)을 적용하고, 기존 위반은
// baseline.xml 로 동결해 CI 를 그린으로 시작한다. 전체 실행: ./gradlew detekt
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        parallel = true
        // 멀티모듈이라 baseline 은 모듈별 파일로 둔다(단일 공유 파일은 모듈끼리 덮어씀).
        // 기존 위반을 동결하고 새 위반만 실패시킨다. 갱신: ./gradlew detektBaseline
        baseline = file("detekt-baseline.xml")
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "17"
        reports {
            xml.required.set(true)
            html.required.set(true)
            sarif.required.set(false)
            txt.required.set(false)
        }
    }
}
