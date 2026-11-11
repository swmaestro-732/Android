package com.chillsam.courmy.main.entity.aichat

/**
 * 커미 AI 채팅의 현재 단계. 서버 응답의 `step` 으로 내려오며, 앱은 이 값으로 화면을 분기한다.
 *
 * 흐름은 REGION → THEME → PLACE → (PLACE_CONFIRM) → RESULT 지만, 서버가 한 문장에서
 * 여러 정보를 읽으면 단계를 건너뛸 수 있다. 앱은 순서를 가정하지 말고 내려온 step 을 그대로 그린다.
 * [UNKNOWN] 은 미래에 추가될 step 을 앱이 깨지지 않게 받기 위한 폴백이다.
 */
enum class AiChatStep {
    /** 지역 질문. 인사말·질문·지역 칩은 앱이 소유한다(서버 quickReplies 는 비어 올 수 있음). */
    REGION,

    /** 테마 질문. 칩(12종 + 상관없어요)은 서버가 내려준다. */
    THEME,

    /** 가고 싶은 장소 질문. "없어요" 칩 + 장소 이름 직접 입력. */
    PLACE,

    /** 입력한 장소에 후보가 여러 건일 때 되묻는 단계. 후보 칩만, 직접 입력 불가. */
    PLACE_CONFIRM,

    /** 코스 추천 결과. courses 가 채워지고 "더 추천받기 / 처음부터" 칩. */
    RESULT,

    /** 알 수 없는 단계(미래 호환). 메시지·칩만 그대로 그린다. */
    UNKNOWN,
}
