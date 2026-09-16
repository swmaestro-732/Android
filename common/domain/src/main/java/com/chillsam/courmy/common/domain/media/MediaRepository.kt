package com.chillsam.courmy.common.domain.media

/** 이미지 업로드 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다. */
interface MediaRepository {
    /**
     * 로컬 이미지(content:// 등)를 업로드하고 서버가 접근 가능한 공개 URL 을 돌려준다.
     * 액세스 토큰이 필요하므로 로그인/가입 완료 후에만 호출할 수 있다.
     */
    suspend fun uploadImage(
        localUri: String,
        purpose: UploadPurpose = UploadPurpose.PROFILE,
    ): String
}

/**
 * 업로드 용도. 서버 `UploadPurpose` enum 과 **이름이 그대로** 요청에 실리므로 문자열이 일치해야 한다.
 * 서버는 이 값으로 S3 키 프리픽스(`profile/`·`course/`·`place/`)를 정한다.
 */
enum class UploadPurpose {
    /** 프로필 사진. */
    PROFILE,

    /** 코스 대표 이미지(썸네일). */
    COURSE,

    /** 코스에 담긴 장소별 사진. */
    PLACE,
}
