# 관심 지역 지도 데이터

상태: 생성·지역 코드 대조 완료 (2026-11-02).

`main/data/src/main/assets/area_map.json`은 관심 지역 선택용 경계다. Compose Canvas에서 행정구역 모양만 그리고, 지역명은 하단 목록에 표시한다. 도로·장소·외부 지도 타일은 사용하지 않는다. 행정구역 경계의 측량·주소 판정용 정밀도를 보장하지 않는다.

## 원본과 라이선스

- 원본: [vuski/admdongkor `ver20261001`](https://github.com/vuski/admdongkor/tree/3bc00489b76696db864bdbea4b19985cdfa8b403/ver20261001), 2026-10-01 기준 행정동 경계, WGS84(EPSG:4326), 3,559개 feature.
- 고정 커밋: `3bc00489b76696db864bdbea4b19985cdfa8b403` (2026-10-17).
- 원본 GeoJSON SHA-256: `843ece3a0b3429742cd4a1b699cc13987a88b5e5401f382e7fdaaa194ea5e605`.
- 데이터 라이선스: [CC BY 4.0 + SGIS 공공누리 제1유형 출처표시](https://github.com/vuski/admdongkor/blob/3bc00489b76696db864bdbea4b19985cdfa8b403/LICENSE-DATA). 저장소 소스 코드의 MIT 라이선스와 구분한다.
- 원문을 `main/data/src/main/assets/area_map_LICENSE.txt`에 함께 배포한다. 화면 하단 출처 문구는 사용자 요청으로 제거했으며 배포 asset과 이 문서에 출처·라이선스를 보존한다.

출처 표기: 통계청 통계지리정보서비스(SGIS, https://sgis.kostat.go.kr)의 공공누리 제1유형 행정동 경계를 vuski/admdongkor(https://github.com/vuski/admdongkor)가 가공한 CC BY 4.0 데이터다. Courmy는 시·도 및 시·군·구별 병합, 경계 단순화, 좌표 반올림을 수행했다.

## 코드와 계층

지역명·선택 코드의 원본은 사용자가 제공한 `법정동코드 전체자료.txt`다. 자료는 CP949, 탭 구분, `법정동코드 / 법정동명 / 폐지여부` 열을 갖는다. 원본 SHA-256은 `8cfd829c797270b56243a46e9f1e4e95377c2135153c36909021a35a1e32966a`이며 2026-11-02에 판독했다. 파일 자체에 발행일이 없어 별도 기준일을 추정하지 않는다.

- 전체 53,387행 중 존재 20,560행, 폐지 32,827행이다. 존재하는 10자리 코드 중 끝 2자리가 `00`이고 끝 5자리가 `00000`이 아닌 **읍·면·동 5,067개**를 `legal_areas.json`에 담는다.
- 법정리 15,209개와 시·도·시·군·구 상위 행은 전송용 asset에서 제외한다. 기존 서버 지역 마스터는 읍·면·동 단위이며 법정리 코드를 지원하지 않는다.
- 5,067개 코드 집합은 읽기 전용으로 확인한 BackEnd `V13__area_seed.sql`의 활성 코드 집합과 완전히 일치한다(추가·누락 0). 서버 시드 SHA-256은 `c606ca6003631b07c7b7d5a83f055e91490ae1f657f89d48eb4cec1b0287cbb7`이다. 생성 과정은 서버 저장소를 입력으로 사용하지 않으며 백엔드를 수정하지 않는다. 이는 정적 파일 대조 결과이며 배포된 서버 DB의 상태를 보증하지 않는다.
- 지도에는 활성 읍·면·동의 부모 이름으로 파생한 시·도 16개와 시·군·구 255개, 총 271개를 표시한다. geometry 원본의 `sido` 2자리·`sgg` 5자리가 자료의 코드·지역명과 일치함을 검사한다. 기존 geometry 재생성 결과는 byte 단위로 동일하다.
- 전남광주통합특별시(`12`), 인천 제물포구·영종구·서해구·검단구, 화성시 4개 구가 이 자료에 포함되어 있다. 과거 시·도/시·군·구 체계로 임의 대체하지 않는다.
- 세종의 부모 원본 행은 `3611000000 / 세종특별자치시`다. 읍·면·동 앞에 시·군·구 이름이 없으므로 지도에서는 시·도 `36`만 표시하며, 전송 시 `36110…`으로 시작하는 활성 읍·면·동 코드로 펼친다.
- 일반구가 있는 시는 `수원시 장안구`처럼 5자리 단위를 유지한다. `수원시` 같은 중간 상위 시 13개는 별도 중복 표시하지 않는다.
- `adm_cd`는 통계청 코드이고 `adm_cd2`는 행정동 코드다. 이 두 필드 전체를 법정동 선택 코드로 보내지 않는다. 화면의 선택 깊이는 시·군·구까지다.
- 시·도 전체 선택은 화면에서 상위 항목 하나로 표현하고, 전송 시 선택 범위에 속하는 **활성 10자리 읍·면·동 법정동코드 배열**로 펼친다. 전체 선택 후 특정 시·군·구를 누르면 그 하위 범위만 제외한다. 2자리 시·도 코드를 서버에 직접 보내지 않는다.
- 저장된 과거 동 선택의 유지·삭제 정책은 UI 명세를 따른다. 이 파일로 일부 동 선택을 시·군·구 전체로 조용히 확장하지 않는다.

`legal_areas.json`은 `code`, `name`, `fullName`, `level: "DONG"`을 가진 객체 배열이다. 5,067개·544,488바이트이며 SHA-256은 `c92f545495961b560aacf3582cbd116e4db3623fbb24c3b503de989576c7a7d3`이다. 선택 범위를 코드 prefix로 매칭하고 중복을 제거한다. 폐지 코드를 신규 선택·전송 코드로 생성하지 않는다.

## 가공·형식

Shapely `unary_union`으로 행정동을 코드별 병합하고 `simplify(0.002, preserve_topology=True)`로 단순화한다. 작은 섬을 면적 기준으로 삭제하지 않으며 내부 구멍도 유지한다. 좌표는 소수 6자리로 반올림하고 모든 결과 polygon의 유효성과 면적을 검사한다. 반올림·위경도 순서 전환 후 `orient(sign=1.0)`을 적용하여 내부 ring이 외부 ring과 반대 방향을 따르도록 한다. 지역 간 공유 경계는 각각 단순화하므로 미세한 겹침/틈이 생길 수 있다.

최상위는 지역 객체 배열이며 각 객체는 `code`, `name`, `fullName`, `level`, `polygons`를 가진다. `polygons`의 원소는 `{outer, holes}`이고, 각 ring은 **`[위도, 경도]`** 좌표 배열이다(GeoJSON의 경도·위도 순서와 반대). ring은 시작점과 끝점이 같은 닫힌 경계이며 최소 4개 좌표다. 섬 때문에 한 지역에 여러 polygon이 있을 수 있다.

원본 전국 경계는 남서 `(33.111930, 124.609790)`부터 북동 `(38.616952, 131.871294)`까지다. 단순화 결과는 남서 `(33.112307, 124.609790)`부터 북동 `(38.616952, 131.870493)`까지다. 제주·서해 도서·울릉도·독도 모두 포함하도록 지도 카메라를 맞춘다.

## 재생성

Python과 Shapely는 빌드 도구용이며 앱 의존성이 아니다. 저장소 밖 임시 가상환경에서 설치한다.

```bash
python3 -m venv /tmp/courmy-area-tools
/tmp/courmy-area-tools/bin/pip install 'shapely==2.0.7'
curl -fL 'https://raw.githubusercontent.com/vuski/admdongkor/3bc00489b76696db864bdbea4b19985cdfa8b403/ver20261001/HangJeongDong_ver20261001.geojson' -o /tmp/courmy-area-source.geojson
/tmp/courmy-area-tools/bin/python scripts/generate_area_map.py \
  /tmp/courmy-area-source.geojson \
  /path/to/법정동코드\ 전체자료.txt \
  main/data/src/main/assets/area_map.json
python3 scripts/generate_legal_areas.py \
  /path/to/법정동코드\ 전체자료.txt \
  main/data/src/main/assets/legal_areas.json
```

생성 결과는 271개 지역·1,195,670바이트, 2,461개 polygon·14개 hole·48,709개 좌표다. 전국 시·도 레이어에도 작은 섬이 많으므로 지도에 보이는 단계만 렌더링한다. 새 경계/법정동 자료로 갱신할 때 원본 날짜·라이선스·코드 차이·예외를 다시 확인한다. 스크립트는 누락 코드·알 수 없는 코드·지역명 불일치(띄어쓰기 차이는 허용)·잘못된 geometry를 실패로 처리하며 가짜 경계로 대체하지 않는다.

생성 파일 SHA-256: `d4928e68e1f45ab849d3ed1bafbbd0bf0b598ce155c70e3b6c21c816c80cae50`.
