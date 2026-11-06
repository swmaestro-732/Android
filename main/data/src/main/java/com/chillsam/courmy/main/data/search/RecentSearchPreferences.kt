package com.chillsam.courmy.main.data.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 최근 검색어 로컬 저장. 순서(최신 앞)를 지켜야 해서 StringSet 대신 JSON 배열 문자열로 둔다.
 * 깨진 값이 저장돼 있으면 빈 목록으로 복구한다(검색 진입을 막지 않게).
 */
@Singleton
class RecentSearchPreferences
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val preferences = context.getSharedPreferences("search_settings", Context.MODE_PRIVATE)

        var keywords: List<String>
            get() =
                preferences
                    .getString(KEY_RECENT, null)
                    ?.let { raw ->
                        runCatching { Json.decodeFromString(ListSerializer(String.serializer()), raw) }
                            .getOrDefault(emptyList())
                    }.orEmpty()
            set(value) {
                check(
                    preferences
                        .edit()
                        .putString(KEY_RECENT, Json.encodeToString(ListSerializer(String.serializer()), value))
                        .commit(),
                )
            }

        private companion object {
            const val KEY_RECENT = "recent_keywords"
        }
    }
