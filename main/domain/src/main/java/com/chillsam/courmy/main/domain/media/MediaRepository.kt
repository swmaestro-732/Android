package com.chillsam.courmy.main.domain.media

/** 이미지 업로드 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다. */
interface MediaRepository {
    /**
     * 로컬 이미지(content:// 등)를 업로드하고 서버가 접근 가능한 공개 URL 을 돌려준다.
     * 액세스 토큰이 필요하므로 로그인/가입 완료 후에만 호출할 수 있다.
     */
    suspend fun uploadProfileImage(localUri: String): String
}
