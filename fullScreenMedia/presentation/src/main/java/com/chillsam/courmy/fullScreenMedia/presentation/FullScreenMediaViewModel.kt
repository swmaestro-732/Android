package com.chillsam.courmy.fullScreenMedia.presentation

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.favorite.GetFavoriteItemsUseCase
import com.chillsam.courmy.common.domain.favorite.RegisterFavoriteItemUseCase
import com.chillsam.courmy.common.domain.favorite.RemoveFavoriteItemUseCase
import com.chillsam.courmy.common.domain.helper.MessageHelper
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.domain.helper.ResourceHelper
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.common.entity.favorite.FavoriteItemVO
import com.chillsam.courmy.common.entity.media.MediaItemVO
import com.chillsam.courmy.common.entity.media.MediaType
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.fullScreenMedia.domain.FullScreenMediaOrigin
import com.chillsam.courmy.fullScreenMedia.domain.FullScreenMediaPage
import com.chillsam.courmy.fullScreenMedia.domain.tti.FullScreenMediaTTIPage
import com.chillsam.courmy.tti.TTIHelper
import com.chillsam.courmy.tti.TimelineCategory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = FullScreenMediaViewModel.Factory::class)
class FullScreenMediaViewModel
    @AssistedInject
    constructor(
        @Assisted private val args: FullScreenMediaPage.Args,
        private val getFavoriteItemsUseCase: GetFavoriteItemsUseCase,
        private val registerFavoriteItemUseCase: RegisterFavoriteItemUseCase,
        private val removeFavoriteItemUseCase: RemoveFavoriteItemUseCase,
        private val messageHelper: MessageHelper,
        private val navigationHelper: NavigationHelper,
        private val ttiHelper: TTIHelper,
        private val resourceHelper: ResourceHelper,
    ) : MviViewModel<FullScreenMediaIntent, FullScreenMediaUIState, FullScreenMediaReducerEvent>(
            FullScreenMediaUIState.empty,
        ) {
        init {
            bootstrap()
        }

        override fun onIntent(intent: FullScreenMediaIntent) {
            when (intent) {
                is FullScreenMediaIntent.AddFavorite -> {
                    addFavorite(intent.item)
                }

                is FullScreenMediaIntent.DeleteFavorite -> {
                    deleteFavorite(intent.url)
                }

                is FullScreenMediaIntent.ClickBackButton -> {
                    clickBackNavigation()
                }

                is FullScreenMediaIntent.SelectPage -> {
                    dispatch(FullScreenMediaReducerEvent.PageSelected(intent.index))
                }
            }
        }

        override fun reduce(
            state: FullScreenMediaUIState,
            event: FullScreenMediaReducerEvent,
        ): FullScreenMediaUIState =
            when (event) {
                is FullScreenMediaReducerEvent.Initialized -> {
                    FullScreenMediaUIState.ready(event.mediaItems, event.initialIndex, event.swipeEnabled)
                }

                FullScreenMediaReducerEvent.EmptyMediaResolved -> {
                    state.copy(isLoading = false)
                }

                is FullScreenMediaReducerEvent.FavoritesChanged -> {
                    state.copy(favoriteUrls = event.urls.asImmutableUrls())
                }

                is FullScreenMediaReducerEvent.PageSelected -> {
                    state.copy(currentIndex = event.index)
                }
            }

        private fun bootstrap() {
            ttiHelper.startTTITimeline(FullScreenMediaTTIPage, TimelineCategory.API_REQUEST_READY_TIME)
            ttiHelper.endTTITimeline(FullScreenMediaTTIPage, TimelineCategory.API_REQUEST_READY_TIME)
            ttiHelper.startTTITimeline(FullScreenMediaTTIPage, TimelineCategory.API_RESPONSE_TIME)
            when (args.origin) {
                FullScreenMediaOrigin.FAVORITE -> bootstrapFromFavorites()

                FullScreenMediaOrigin.SEARCH,
                FullScreenMediaOrigin.DEEP_LINK,
                -> bootstrapSingleItem()
            }
            ttiHelper.endTTITimeline(FullScreenMediaTTIPage, TimelineCategory.API_RESPONSE_TIME)
        }

        private fun bootstrapFromFavorites() {
            viewModelScope.launch {
                val mediaItems = getFavoriteItemsUseCase().first().map { it.toMediaItemVO() }
                if (mediaItems.isEmpty()) {
                    resolveEmpty()
                    return@launch
                }
                val initialIndex = mediaItems.indexOfFirst { it.urlKey == args.url }.coerceAtLeast(0)
                dispatch(
                    FullScreenMediaReducerEvent.Initialized(
                        mediaItems = mediaItems,
                        initialIndex = initialIndex,
                        swipeEnabled = true,
                    ),
                )
                observeFavoriteUrls()
            }
        }

        private fun bootstrapSingleItem() {
            val mediaItems = buildSingleItem(args)
            if (mediaItems.isEmpty()) {
                resolveEmpty()
                return
            }
            dispatch(
                FullScreenMediaReducerEvent.Initialized(
                    mediaItems = mediaItems,
                    initialIndex = 0,
                    swipeEnabled = false,
                ),
            )
            observeFavoriteUrls()
        }

        private fun observeFavoriteUrls() {
            getFavoriteItemsUseCase()
                .map { items -> items.mapTo(HashSet(items.size)) { it.urlKey } }
                .distinctUntilChanged()
                .onEach { urls -> dispatch(FullScreenMediaReducerEvent.FavoritesChanged(urls)) }
                .launchIn(viewModelScope)
        }

        private fun resolveEmpty() {
            messageHelper.showOneButtonDialog(
                titleText = "",
                descText = resourceHelper.getString(StringResource.MEDIA_EMPTY),
                cantIgnore = true,
                onClickButton = { navigationHelper.navigateToBack() },
            )
            dispatch(FullScreenMediaReducerEvent.EmptyMediaResolved)
        }

        private fun addFavorite(item: MediaItemVO) {
            viewModelScope.launch {
                val favoriteItem =
                    FavoriteItemVO(
                        type = item.type,
                        title = item.title,
                        urlKey = item.urlKey,
                        thumbnailUrl = item.thumbnailImageUrl,
                        contentsImageUrl = item.contentsImageUrl,
                        dateTime = item.dateTime,
                    )
                registerFavoriteItemUseCase(favoriteItem)
            }
        }

        private fun deleteFavorite(url: String) {
            viewModelScope.launch {
                removeFavoriteItemUseCase(url)
            }
        }

        private fun clickBackNavigation() {
            navigationHelper.navigateToBack()
        }

        private fun buildSingleItem(args: FullScreenMediaPage.Args): List<MediaItemVO> {
            if (args.url.isBlank()) return emptyList()
            return listOf(
                MediaItemVO(
                    type = args.type,
                    title = args.title,
                    urlKey = args.url,
                    thumbnailImageUrl = args.thumbnailImageUrl,
                    contentsImageUrl = args.contentsImageUrl.ifBlank { args.url },
                ),
            )
        }

        @AssistedFactory
        interface Factory {
            fun create(args: FullScreenMediaPage.Args): FullScreenMediaViewModel
        }
    }

private fun FavoriteItemVO.toMediaItemVO(): MediaItemVO =
    MediaItemVO(
        type = type,
        title = title,
        urlKey = urlKey,
        thumbnailImageUrl = thumbnailUrl,
        // 저장된 contentsImageUrl 을 우선 사용. 구버전 저장본(빈 값)은 이미지=원본(urlKey), 동영상=썸네일로 폴백.
        contentsImageUrl = contentsImageUrl.ifBlank { if (type == MediaType.VIDEO) thumbnailUrl else urlKey },
        dateTime = dateTime,
    )
