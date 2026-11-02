package com.chillsam.courmy.main.data.profile

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.main.entity.area.AreaVO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.interestDataStore: DataStore<Preferences> by preferencesDataStore(name = "profile_interest")

/**
 * 관심 테마·지역을 기기에 보관하는 로컬 데이터소스.
 *
 * 기존 가입·저장 흐름과 호환하기 위한 캐시다. 프로필 표시의 정본은 GET /api/v1/users이며,
 * 이 캐시로 서버 응답을 덮거나 서버 조회 실패를 성공으로 바꾸지 않는다.
 *
 * 지역은 라벨만으로는 부족하다 — 회원가입 요청에 실을 법정동코드([AreaVO.code])까지 있어야 해서
 * [AreaVO] 를 통째로 JSON 직렬화해 담는다.
 */
class InterestPreferencesDataStore(
    context: Context,
) {
    private val dataStore = context.interestDataStore

    suspend fun getThemes(): List<String> =
        dataStore.data
            .map { preferences ->
                preferences[KEY_THEMES]
                    .decodeOrEmpty<String>()
                    .mapNotNull { CourseTagVO.fromCode(it)?.code }
                    .distinct()
            }.first()

    suspend fun setThemes(themes: List<String>) {
        dataStore.edit { it[KEY_THEMES] = json.encodeToString(themes) }
    }

    suspend fun getRegions(): List<AreaVO> = dataStore.data.map { it[KEY_REGIONS].decodeOrEmpty<AreaVO>() }.first()

    suspend fun setRegions(regions: List<AreaVO>) {
        dataStore.edit { it[KEY_REGIONS] = json.encodeToString(regions) }
    }

    /**
     * 저장된 문자열이 없거나(첫 실행) 예전 형식이라 파싱에 실패해도 화면이 죽지 않게 빈 목록으로 떨어뜨린다.
     * 관심사는 사용자가 다시 고르면 그만인 값이라, 복구보다 화면이 뜨는 쪽이 낫다.
     */
    private inline fun <reified T> String?.decodeOrEmpty(): List<T> {
        if (this.isNullOrBlank()) return emptyList()
        return runCatching { json.decodeFromString<List<T>>(this) }.getOrDefault(emptyList())
    }

    private companion object {
        val KEY_THEMES = stringPreferencesKey("themes")
        val KEY_REGIONS = stringPreferencesKey("regions")
        val json = Json { ignoreUnknownKeys = true }
    }
}
