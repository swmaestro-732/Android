package com.chillsam.courmy.main.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.chillsam.courmy.main.entity.area.AreaVO

/**
 * 회원가입 플로우에서 고른 관심 테마·지역을 완료 화면(FS-08)까지 전달하는 임시 인메모리 홀더.
 *
 * 테마→지역→완료가 서로 다른 라우트(=별개 컴포저블 스코프)라 선택값을 직접 넘길 수 없어,
 * 각 단계의 "다음"에서 이 홀더에 담고 완료 화면이 읽는다. 실 회원가입 API·프로필 저장이
 * 붙기 전까지의 시연용이다.
 */
object SignupSelectionStore {
    var nickname by mutableStateOf("")
    var handle by mutableStateOf("")
    var profileImageUrl by mutableStateOf<String?>(null)
    var themes by mutableStateOf<List<String>>(emptyList())

    /**
     * 고른 관심 지역. 회원가입 요청의 `areaCodes` 에 실을 법정동코드가 필요해 라벨이 아니라
     * [AreaVO] 를 통째로 담는다(보낼 때는 [regionCodes] 로 꺼낸다).
     */
    var regions by mutableStateOf<List<AreaVO>>(emptyList())

    /**
     * 서버에 보낼 관심 지역 코드.
     *
     * 검색 폴백으로 고른 항목은 코드가 없어(AreaSearchViewModel 주석 참고) 여기서 빠진다 —
     * 빈 문자열을 보내면 서버가 없는 지역으로 처리할 수 있어서다.
     */
    val regionCodes: List<String>
        get() = regions.mapNotNull { it.code.takeIf(String::isNotBlank) }

    /** 가입 완료/취소 시 다음 세션을 위해 비운다. */
    fun clear() {
        nickname = ""
        handle = ""
        profileImageUrl = null
        themes = emptyList()
        regions = emptyList()
    }
}
