package com.chillsam.courmy.main.presentation.map

/**
 * 홈 "근처 저장 장소" 카드에서 지도 탭으로 넘어올 때 선택할 장소를 전달하는 임시 인메모리 홀더.
 *
 * 지도 탭은 bottom tab 라우트라 (path, args)가 곧 백스택 키다 — 라우트 인자로 실으면 인자 없는
 * 탭 엔트리와 별개의 지도 화면이 스택에 하나 더 생긴다. 그래서 회원가입 플로우의
 * [com.chillsam.courmy.main.presentation.login.SignupSelectionStore] 처럼 홀더로 건네고,
 * 지도가 장소 목록을 확보한 시점에 꺼내 선택한다.
 *
 * 좌표는 싣지 않는다 — 홈 카드 VO 에 좌표가 없고, 지도는 어차피 뷰포트 조회 결과에서 같은
 * id 를 찾아야 핀·카메라를 움직일 수 있다(홈의 근처 장소는 현재 위치 인근이라 첫 조회에 들어온다).
 */
object MapFocusRequestStore {
    private var placeId: Long? = null

    /** 지도 진입 전에 호출한다. 마지막 요청만 남는다. */
    fun request(placeId: Long) {
        this.placeId = placeId
    }

    /** 꺼내면서 비운다 — 한 번의 진입에만 적용되고 다음 지도 방문에 남지 않게. */
    fun consume(): Long? = placeId.also { placeId = null }
}
