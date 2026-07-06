# courmy

## 개발 환경

- 언어: Kotlin
- UI: Jetpack Compose (Material 3)
- 최소 지원 버전: Android 8.0 (minSdk 26)
- 타겟 버전: SDK 36

## 프로젝트 구조

```
app/
├─ src/main/
│  ├─ java/com/chillsam/courmy/
│  │  ├─ MainActivity.kt      # 앱 진입점(시작 화면)
│  │  └─ ui/theme/            # 색상·글꼴·테마 정의
│  ├─ res/                    # 아이콘, 문자열, 색상 등 리소스
│  └─ AndroidManifest.xml     # 앱 구성 정보
└─ build.gradle.kts           # 앱 모듈 빌드 설정
```

## 빌드 & 실행

Android Studio에서 프로젝트를 열고 `Run` 하거나, 터미널에서:

```bash
./gradlew installDebug
```
