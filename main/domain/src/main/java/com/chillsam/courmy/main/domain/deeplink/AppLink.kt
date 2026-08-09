package com.chillsam.courmy.main.domain.deeplink

import com.chillsam.courmy.common.domain.navigation.NavRoute
import java.net.URLEncoder

/**
 * 앱 화면을 가리키는 공유용 웹 링크.
 *
 * 매니페스트의 App Links 와 같은 호스트를 쓰고, path·query 는
 * [NavRoute] 를 그대로 옮긴다 — deep-link 해석(`Uri.resolveRoute`)이 path segment 와 query 를
 * 라우트 인자로 되돌리므로, 여기서 만든 링크를 누르면 그 화면으로 바로 들어온다.
 *
 * 호스트 검증(`autoVerify`)이 끝난 기기는 앱으로, 아니면 웹으로 열린다.
 */
object AppLink {
    /** App Links 로 검증된 호스트. 매니페스트의 `android:host` 와 반드시 같아야 한다. */
    const val HOST = "https://www.courmy.com"

    /**
     * [route] 를 공유 가능한 절대 URL 로 만든다.
     *
     * 인자는 query 로 붙이며 값은 퍼센트 인코딩한다 — 핸들·제목에 공백이나 한글이 섞여도
     * 링크가 중간에서 잘리지 않는다.
     */
    fun of(route: NavRoute): String {
        val path = route.path.removeSuffix("/")
        if (route.args.isEmpty()) return HOST + path
        val query =
            route.args.entries.joinToString("&") { (key, value) ->
                "${key.encode()}=${value.encode()}"
            }
        return "$HOST$path?$query"
    }

    private fun String.encode(): String = URLEncoder.encode(this, Charsets.UTF_8.name())
}
