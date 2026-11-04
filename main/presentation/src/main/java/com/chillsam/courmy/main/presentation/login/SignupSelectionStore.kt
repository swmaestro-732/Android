package com.chillsam.courmy.main.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.chillsam.courmy.main.entity.area.AreaVO

/**
 * 회원가입 플로우에서 고른 관심 테마·지역을 완료 화면(FS-08)까지 전달하는 임시 인메모리 홀더.
 *
 * 테마→지역→완료가 서로 다른 라우트(=별개 컴포저블 스코프)라 선택값을 직접 넘길 수 없어,
 * 선택값을 이 홀더에 담고 완료 화면의 회원가입 API 요청에 사용한다.
 * 프로세스 종료 후 미완료 가입 정보의 복원은 제공하지 않는다.
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
     * 가입 data 레이어에 전달할 관심 지역 선택 코드.
     *
     * 화면에서는 시·도 전체를 prefix 하나로 유지하고, 요청 직전에 활성 법정동 코드 배열로 펼친다.
     * 이름은 표시용이며 요청에는 포함하지 않는다.
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
