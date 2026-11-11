package com.chillsam.courmy.common.domain.translation

import kotlinx.coroutines.flow.Flow

/**
 * 사용자가 고른 앱 표시 언어를 보관·관찰한다(구현은 data 레이어의 로컬 저장소).
 *
 * 설정 화면이 [setLanguage] 로 바꾸고, 콘텐츠 화면은 [language] 를 구독해 번역 노출을 결정한다.
 */
interface LanguageRepository {
    /** 이미 읽은 현재 언어. 화면 밖 리소스 조회가 저장소 I/O를 기다리지 않게 한다. */
    val currentLanguage: AppLanguage

    /** 현재 선택된 언어. 저장값이 없으면 [AppLanguage.KOREAN]. */
    val language: Flow<AppLanguage>

    suspend fun setLanguage(language: AppLanguage)
}
