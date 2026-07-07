package com.chillsam.courmy.intro.data

import com.chillsam.courmy.intro.domain.IntroRepository

class IntroRepositoryImpl(
    val dataSource: IntroDataSource,
) : IntroRepository {
    override suspend fun getIntro() = dataSource.getIntro().toVO()
}
