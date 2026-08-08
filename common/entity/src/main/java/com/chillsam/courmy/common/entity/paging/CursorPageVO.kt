package com.chillsam.courmy.common.entity.paging

/**
 * 커서 페이징 응답 한 페이지.
 *
 * 서버 목록 API 는 모두 `nextCursor`·`hasNext` 를 같은 모양으로 준다(홈 피드·저장함·팔로우·장소 검색).
 * 화면마다 같은 데이터 클래스를 따로 두지 않도록 여기 하나만 둔다.
 *
 * [nextCursor] 는 서버가 만든 **불투명 문자열**이다. 형식(마지막 id인지 base64 키셋인지)은
 * 엔드포인트마다 다르고 바뀔 수 있으므로, 앱은 해석하지 않고 다음 요청에 그대로 되돌려준다.
 *
 * [hasNext] 가 false 면 [nextCursor] 도 null 이어야 정상이다. 서버가 어긋나게 주더라도
 * 무한 재요청에 빠지지 않도록, 변환하는 쪽에서 커서가 없으면 끝으로 처리한다.
 */
data class CursorPageVO<T>(
    val items: List<T> = emptyList(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
)
