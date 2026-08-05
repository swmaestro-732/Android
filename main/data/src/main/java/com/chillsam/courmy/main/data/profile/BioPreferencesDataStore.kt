package com.chillsam.courmy.main.data.profile

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.bioDataStore: DataStore<Preferences> by preferencesDataStore(name = "profile_bio")

/**
 * 한 줄 소개(bio)를 기기에 보관하는 로컬 데이터소스.
 *
 * TODO-API-SPEC: **서버에 bio 필드가 없어서 두는 임시 저장소다.**
 * `PATCH /api/v1/users`(UpdateProfileRequest)와 `GET /service/v1/mypage`(MyPageProfileResponse)
 * 어디에도 소개 필드가 없어, 지금은 이 기기에만 남고 다른 기기·다른 사용자에게는 보이지 않는다.
 * 서버에 필드가 추가되면 이 파일과 [ProfileRepositoryImpl] 의 병합·저장 호출을 함께 제거하고
 * 응답의 bio 를 그대로 쓰면 된다. [wiki-needed]
 */
class BioPreferencesDataStore(
    context: Context,
) {
    private val dataStore = context.bioDataStore

    suspend fun getBio(): String = dataStore.data.map { it[KEY_BIO].orEmpty() }.first()

    suspend fun setBio(bio: String) {
        dataStore.edit { it[KEY_BIO] = bio }
    }

    private companion object {
        val KEY_BIO = stringPreferencesKey("bio")
    }
}
