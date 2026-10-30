package com.chillsam.courmy.main.data.update

import com.chillsam.courmy.main.entity.update.AppVersionCheckVO
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteConfigAppVersionRepositoryTest {
    @Test
    fun `설치 코드보다 원격 코드가 높을 때만 업데이트 가능하다`() =
        runTest {
            for (latest in listOf(9L, 10L, 11L)) {
                val result = RemoteConfigAppVersionRepository({ 10L }, { latest to "1.0.9" }).checkVersion()
                assertEquals(AppVersionCheckVO.Checked(10L, latest, "1.0.9"), result)
                assertEquals(latest > 10L, (result as AppVersionCheckVO.Checked).updateAvailable)
            }
        }

    @Test
    fun `자동 조회는 캐시를 허용하고 수동 조회는 캐시 우회 요청과 새 버전을 반환한다`() =
        runTest {
            val requests = mutableListOf<Boolean>()
            val repository =
                RemoteConfigAppVersionRepository(
                    installedVersionCode = { 10L },
                    fetchLatestVersion = { forceRefresh ->
                        requests += forceRefresh
                        if (forceRefresh) 12L to "1.0.2" else 10L to "1.0.0"
                    },
                )

            assertEquals(AppVersionCheckVO.Checked(10L, 10L, "1.0.0"), repository.checkVersion())
            assertEquals(AppVersionCheckVO.Checked(10L, 12L, "1.0.2"), repository.checkVersion(forceRefresh = true))
            assertEquals(listOf(false, true), requests)
        }

    @Test
    fun `미설정 기본값과 음수는 최신이 아니라 확인 불가다`() =
        runTest {
            for (latest in listOf(0L, -1L)) {
                val result = RemoteConfigAppVersionRepository({ 10L }, { latest to "1.0.9" }).checkVersion()
                assertEquals(AppVersionCheckVO.Unavailable, result)
            }
        }

    @Test
    fun `조회 실패는 확인 불가로 반환한다`() =
        runTest {
            val repository = RemoteConfigAppVersionRepository({ 10L }, { error("fetch failed") })
            assertEquals(AppVersionCheckVO.Unavailable, repository.checkVersion())
        }

    @Test
    fun `응답이 없으면 5초 후 확인 불가로 반환한다`() =
        runTest {
            val repository =
                RemoteConfigAppVersionRepository({ 10L }, { CompletableDeferred<Pair<Long, String>>().await() })
            assertEquals(AppVersionCheckVO.Unavailable, repository.checkVersion())
        }

    @Test
    fun `호출자 취소는 확인 불가로 삼키지 않는다`() =
        runTest {
            val repository = RemoteConfigAppVersionRepository({ 10L }, { throw CancellationException() })
            var cancelled = false
            try {
                repository.checkVersion()
            } catch (_: CancellationException) {
                cancelled = true
            }
            assertTrue(cancelled)
        }
}
