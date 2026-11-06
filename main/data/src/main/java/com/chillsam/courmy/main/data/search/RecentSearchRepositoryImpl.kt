package com.chillsam.courmy.main.data.search

import com.chillsam.courmy.main.domain.search.RecentSearchRepository

class RecentSearchRepositoryImpl(
    private val preferences: RecentSearchPreferences,
) : RecentSearchRepository {
    override fun getAll(): List<String> = preferences.keywords

    override fun replaceAll(keywords: List<String>) {
        preferences.keywords = keywords
    }
}
