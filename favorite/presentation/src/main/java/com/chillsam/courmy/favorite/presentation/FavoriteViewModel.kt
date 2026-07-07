package com.chillsam.courmy.favorite.presentation

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.favorite.GetFavoriteItemsUseCase
import com.chillsam.courmy.common.domain.favorite.RemoveFavoriteItemUseCase
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.fullScreenMedia.domain.FullScreenMediaOrigin
import com.chillsam.courmy.fullScreenMedia.domain.FullScreenMediaPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteItemsUseCase: GetFavoriteItemsUseCase,
    private val removeFavoriteItemUseCase: RemoveFavoriteItemUseCase,
    private val navigationHelper: NavigationHelper,
) : MviViewModel<FavoriteIntent, FavoriteUIState, FavoriteReducerEvent>(FavoriteUIState.empty) {

    init {
        onIntent(FavoriteIntent.Load)
    }

    override fun onIntent(intent: FavoriteIntent) {
        when (intent) {
            FavoriteIntent.Load -> observeFavorites()
            is FavoriteIntent.DeleteFavorite -> deleteFavorite(intent.url)
            is FavoriteIntent.OpenFullScreen -> openFullScreen(intent.url)
        }
    }

    override fun reduce(state: FavoriteUIState, event: FavoriteReducerEvent): FavoriteUIState =
        when (event) {
            FavoriteReducerEvent.LoadingStarted -> state.copy(isLoading = true)
            is FavoriteReducerEvent.ItemsLoaded -> FavoriteUIState.fromItems(event.items)
        }

    private fun observeFavorites() {
        dispatch(FavoriteReducerEvent.LoadingStarted)
        getFavoriteItemsUseCase()
            .onEach { items -> dispatch(FavoriteReducerEvent.ItemsLoaded(items)) }
            .launchIn(viewModelScope)
    }

    private fun deleteFavorite(url: String) {
        viewModelScope.launch {
            removeFavoriteItemUseCase(url)
        }
    }

    private fun openFullScreen(url: String) {
        navigationHelper.navigateTo(
            FullScreenMediaPage.Args(
                origin = FullScreenMediaOrigin.FAVORITE,
                url = url,
            )
        )
    }
}
