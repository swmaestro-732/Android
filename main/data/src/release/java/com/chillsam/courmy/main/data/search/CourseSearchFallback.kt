package com.chillsam.courmy.main.data.search

import com.chillsam.courmy.main.data.search.dto.CourseSearchEnvelope

/**
 * 릴리스용 no-op — 서버 응답을 그대로 통과시킨다(실패하면 예외도 그대로 올라간다).
 *
 * 디버그 빌드에는 예시 응답으로 대체하는 구현이 있다(`src/debug` 의 같은 이름 파일에 배경 설명).
 * 예시 데이터가 릴리스 산출물에 **컴파일조차 되지 않게** 하려고 소스셋으로 갈라 뒀다.
 */
internal object CourseSearchFallback {
    suspend fun resolve(fetch: suspend () -> CourseSearchEnvelope): CourseSearchEnvelope = fetch()
}
