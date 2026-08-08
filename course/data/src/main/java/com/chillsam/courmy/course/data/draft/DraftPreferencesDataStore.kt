package com.chillsam.courmy.course.data.draft

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.draftDataStore: DataStore<Preferences> by preferencesDataStore(name = "course_drafts")

/**
 * 초안 목록을 JSON 한 덩어리로 Preferences DataStore 에 보관한다.
 *
 * 건수가 많지 않고(사용자가 직접 만든 초안) 항상 통째로 읽고 쓰는 접근이라 Room 까지 가지 않는다.
 */
class DraftPreferencesDataStore(
    context: Context,
    private val json: Json,
) : DraftLocalStore {
    private val dataStore = context.draftDataStore

    override suspend fun load(): List<StoredDraft> =
        dataStore.data
            .map { it[KEY_DRAFTS] }
            .first()
            ?.let { raw ->
                // 저장 포맷이 바뀌었거나 값이 깨졌으면 초안을 잃을지언정 앱이 죽지는 않게 한다.
                runCatching { json.decodeFromString<List<StoredDraft>>(raw) }
                    .onFailure { Log.w(TAG, "임시저장 초안을 읽지 못해 비우고 시작한다", it) }
                    .getOrNull()
            }.orEmpty()

    override suspend fun save(drafts: List<StoredDraft>) {
        val raw = json.encodeToString(drafts)
        dataStore.edit { it[KEY_DRAFTS] = raw }
    }

    private companion object {
        const val TAG = "DraftStore"
        val KEY_DRAFTS = stringPreferencesKey("drafts")
    }
}
