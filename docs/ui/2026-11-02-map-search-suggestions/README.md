# 지도 검색 제안과 카메라 이동 검증

2026-11-02 · `fix/SCRUM-643-map-search-suggestions` · 최신 debug APK · 실제 에뮬레이터/서버 데이터.

## 동작

- 장소 입력이 500ms 멈추면 `/places?q=…&size=10`으로 제안을 표시한다. 새 입력은 이전 요청과 결과를 취소한다.
- 제안 선택은 원래 키워드를 유지하며 선택 장소로 이동한다. 화면 밖의 목적지는 먼저 축소(400ms)한 뒤 목적지로 이동·확대(700ms)한다.
- 일반 검색은 현재 화면의 `/places/map` 결과를 우선한다. 장소와 클러스터가 모두 없을 때만 `/places`의 첫 유효 결과로 한 번 이동한다.
- 이동 중 핀·클러스터·기존 결과를 숨긴다. 도착한 화면의 `/places/map` 응답 후 결과를 표시한다.
- 코스 일반 검색은 `/places`, 새로운 위치 추가 확인만 `/places/search`를 사용한다.

## 실제 화면 확인

카페 제안의 하나네 과자점을 선택하여 부산 중심에서 공주로 이동했다. 녹화에서 이동 중 핀 없이 로딩을 표시하고, 도착 후 카페 핀과 장소 카드가 표시되는 것을 확인했다.

1. [검색 제안 10개](01-suggestions.png)
2. [이동 중: 핀 숨김과 로딩](02-moving.png)
3. [도착 후: 핀과 장소 카드](03-arrived.png)

첫 두 이미지는 실제 화면 녹화의 프레임이며 마지막 이미지는 도착 후 직접 캡처했다. 서버 버전 제한을 통과하는 로컬 미리보기용 `-PappVersionCode=2100000000`을 사용했다. 저장소의 출시 버전 설정은 변경하지 않았다.

## 자동 검증

다음 작업을 통과했다. 관련 모듈 테스트 총 230건, 실패/오류 0건.

```bash
./gradlew :main:presentation:testDebugUnitTest :main:domain:test \
  :main:data:testDebugUnitTest :course:presentation:testDebugUnitTest \
  :app:assembleDebug :app:lintDebug detekt --console=plain
```

변경 Kotlin 파일의 `scripts/ktlint.sh`, `git diff HEAD --check`, 위키 lint도 통과했다. 독립 읽기 전용 리뷰에서 초기 지도 영역의 회전 복원 문제를 발견해 `rememberSaveable`로 수정했다. 테스트는 499/500ms 경계, 연속 입력과 늦게 도착한 응답, API 경로, 빈 화면 자동 이동, 선택 장소로 직접 이동, 이동 중 요청 취소, 잘못된 도착 이벤트 무시와 한 번만 자동 이동하는 동작을 포함한다.

전체 저장소 `test`는 실행하지 않았으며 위 영향 모듈 범위로 검증했다. 코스 생성 화면, 클러스터만 있는 상태, 제스처로 이동 중단, 화면 회전은 이번 실제 화면 검증에서 미실행이다.

## 문서 동기화

공유 위키의 `api-spec.md`, `android/api-integration.md`, `android/design.md`에 흐름을 반영했다.
[Courmy UI Flow](https://www.notion.so/3f6b448bdf9d81bea9f1f35597ec3d54)에 세 캡처와 검증 범위를 추가했다. 연결 도구가 기존 이미지 블록을 찾지 못해 제자리 교체는 실패했으며, 기존 순서·설명을 유지하고 최신 캡처를 별도 섹션에 추가했다.
