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
 * 업로드 용도. 서버 `UploadPurpose` enum 과 문자열이 일치해야 한다.
 *
 * TODO-API-SPEC: 서버에는 아직 PROFILE 하나뿐이라 코스 썸네일·장소 사진도 PROFILE 로 올린다
 * (S3 키가 `profile/` 프리픽스 아래 쌓인다). 서버에 COURSE 가 추가되면 여기에 케이스를 넣고
 * 코스 업로드의 용도만 바꾸면 된다. [wiki-needed]
 */
enum class UploadPurpose {
    PROFILE,
}
