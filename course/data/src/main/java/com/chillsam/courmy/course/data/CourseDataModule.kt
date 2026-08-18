package com.chillsam.courmy.course.data

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.course.data.courseCreate.CourseCreateApiService
import com.chillsam.courmy.course.data.courseCreate.CourseCreateDataSource
import com.chillsam.courmy.course.data.courseDetail.CourseDetailApiService
import com.chillsam.courmy.course.data.courseDetail.CourseDetailDataSource
import com.chillsam.courmy.course.data.courseManage.CourseManageDataSource
import com.chillsam.courmy.course.data.draft.DraftApiService
import com.chillsam.courmy.course.data.draft.DraftDataSource
import com.chillsam.courmy.course.data.follow.FollowDataSource
import com.chillsam.courmy.course.data.place.PlaceApiService
import com.chillsam.courmy.course.data.place.PlaceDataSource
import com.chillsam.courmy.course.data.place.PlaceRepositoryImpl
import com.chillsam.courmy.course.data.recommendedTag.RecommendedTagApiService
import com.chillsam.courmy.course.data.recommendedTag.RecommendedTagDataSource
import com.chillsam.courmy.course.domain.CourseRepository
import com.chillsam.courmy.course.domain.PlaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CourseDataModule {
    @Provides
    @Singleton
    fun provideCourseDetailApiService(retrofit: Retrofit): CourseDetailApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseDetailDataSource(apiService: CourseDetailApiService): CourseDetailDataSource =
        CourseDetailDataSource(apiService)

    @Provides
    @Singleton
    fun provideCourseCreateApiService(retrofit: Retrofit): CourseCreateApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseCreateDataSource(apiService: CourseCreateApiService): CourseCreateDataSource =
        CourseCreateDataSource(apiService)

    @Provides
    @Singleton
    fun provideDraftApiService(retrofit: Retrofit): DraftApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideDraftDataSource(apiService: DraftApiService): DraftDataSource = DraftDataSource(apiService)

    @Provides
    @Singleton
    fun provideCourseRepository(
        courseDetailDataSource: CourseDetailDataSource,
        courseCreateDataSource: CourseCreateDataSource,
        recommendedTagDataSource: RecommendedTagDataSource,
        draftDataSource: DraftDataSource,
        courseManageDataSource: CourseManageDataSource,
        followDataSource: FollowDataSource,
        tokenStore: TokenStore,
    ): CourseRepository =
        CourseRepositoryImpl(
            courseDetailDataSource,
            courseCreateDataSource,
            recommendedTagDataSource,
            draftDataSource,
            courseManageDataSource,
            followDataSource,
        ) { tokenStore.userId }

    @Provides
    @Singleton
    fun providePlaceApiService(retrofit: Retrofit): PlaceApiService = retrofit.create()

    @Provides
    @Singleton
    fun providePlaceDataSource(apiService: PlaceApiService): PlaceDataSource = PlaceDataSource(apiService)

    @Provides
    @Singleton
    fun providePlaceRepository(dataSource: PlaceDataSource): PlaceRepository = PlaceRepositoryImpl(dataSource)
}
