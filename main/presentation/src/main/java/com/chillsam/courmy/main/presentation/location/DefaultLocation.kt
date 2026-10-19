package com.chillsam.courmy.main.presentation.location

/**
 * 위치 권한 연동 전까지 쓰는 기본 기준점(성수동 일대).
 *
 * 지도 첫 진입 카메라와 홈 "근처 저장 장소" 거리 계산이 함께 쓴다. mock 데이터가 아니라
 * 실요청 파라미터로 나가는 값이라 mock 패키지 밖에 둔다.
 *
 * TODO(SCRUM-위치권한): 위치 권한·FusedLocation 이 붙으면 실제 좌표 provider 로 교체한다.
 */
internal object DefaultLocation {
    const val LATITUDE = 37.5443
    const val LONGITUDE = 127.0557
}
