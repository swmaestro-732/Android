pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 네이버 지도 SDK(map-sdk / naver-map-compose) 배포 저장소
        maven { url = uri("https://repository.map.naver.com/archive/maven") }
        // 카카오 로그인 SDK 배포 저장소
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
    }
}

rootProject.name = "Courmy"
include(":app")

include(":common:presentation")
include(":common:domain")
include(":common:data")
include(":common:entity")

include(":main:presentation")
include(":main:domain")
include(":main:data")
include(":main:entity")

include(":course:presentation")
include(":course:domain")
include(":course:data")
include(":course:entity")

include(":tti")

include(":baselineprofile")
