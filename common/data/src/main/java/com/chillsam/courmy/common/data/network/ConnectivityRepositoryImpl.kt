package com.chillsam.courmy.common.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import com.chillsam.courmy.common.domain.network.ConnectivityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [ConnectivityRepository] 구현. 시스템 [ConnectivityManager] 로 현재 활성 네트워크를 확인한다.
 *
 * 값을 캐시하지 않고 매번 물어본다 — 비행기 모드를 풀거나 와이파이를 다시 잡은 직후에 바로
 * 반영돼야 "다시 시도" 가 동작한다.
 */
@Singleton
class ConnectivityRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ConnectivityRepository {
        override fun isOnline(): Boolean {
            val manager = context.getSystemService<ConnectivityManager>() ?: return false
            val capabilities = manager.activeNetwork?.let(manager::getNetworkCapabilities) ?: return false
            // INTERNET 은 "인터넷용 네트워크", VALIDATED 는 "실제로 나갔다 왔음" 을 뜻한다.
            // 둘을 함께 봐야 캡티브 포털·데이터 끊긴 와이파이를 연결된 것으로 오판하지 않는다.
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
    }
