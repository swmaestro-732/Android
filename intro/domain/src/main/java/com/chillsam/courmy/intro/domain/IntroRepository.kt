package com.chillsam.courmy.intro.domain

import com.chillsam.courmy.intro.entity.IntroVO

interface IntroRepository {
    suspend fun getIntro(): IntroVO
}
