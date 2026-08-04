package com.chillsam.courmy.main.presentation.profile

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.presentation.BuildConfig
import com.chillsam.courmy.main.presentation.my.MyViewModel
import java.io.File

/**
 * 더미 모드에서 "프로필 생성·수정이 반영된 것처럼" 보이게 하는 홀더.
 *
 * `/service/v1/mypage` 미배포 동안 마이 화면은 고정 더미([MyProfileVO.sample])를 렌더하므로,
 * 가입 때 정한 닉네임·아이디나 프로필 편집 결과가 화면에 남지 않는다. 그 간극만 메운다.
 *
 * 값은 [mutableStateOf] 로 들고 있어 바뀌면 이를 읽는 컴포저블이 곧바로 다시 그려지고,
 * 동시에 SharedPreferences 에 write-through 해 **앱 재시작·재설치 후에도 유지**된다
 * (인메모리만이면 프로세스가 죽는 순간 가입한 프로필이 사라져 더미로 되돌아간다).
 *
 * 실 API 연동(USE_SAMPLE=false) 시에는 [isActive] 가 false 라 아무 영향이 없다.
 *
 * TODO-API-SPEC: 백엔드 배포 후 USE_SAMPLE 을 끄면 이 홀더와 참조처를 제거한다. [wiki-needed]
 */
object SampleProfileStore {
    private const val TAG = "SampleProfileStore"
    private const val PREFS_NAME = "sample_profile"

    private var prefs: SharedPreferences? = null
    private var appContext: Context? = null

    private var nickname by mutableStateOf<String?>(null)
    private var handle by mutableStateOf<String?>(null)
    private var bio by mutableStateOf<String?>(null)
    private var profileImageUrl by mutableStateOf<String?>(null)

    /** 더미 모드일 때만 동작한다(release 빌드에서는 항상 false). */
    val isActive: Boolean
        get() = BuildConfig.DEBUG && MyViewModel.USE_SAMPLE

    /** Application 에서 1회 호출. 저장돼 있던 값을 인메모리로 복원한다. */
    fun init(context: Context) {
        if (!isActive) return
        appContext = context.applicationContext
        val store = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = store
        nickname = store.getString(KEY_NICKNAME, null)
        handle = store.getString(KEY_HANDLE, null)
        bio = store.getString(KEY_BIO, null)
        profileImageUrl = store.getString(KEY_IMAGE, null)
        Log.d(TAG, "복원: nickname=$nickname handle=$handle")
    }

    /** null 인 인자는 기존 값을 유지한다(부분 수정). */
    fun update(
        nickname: String? = null,
        handle: String? = null,
        bio: String? = null,
        profileImageUrl: String? = null,
    ) {
        if (!isActive) return
        nickname?.let { this.nickname = it }
        handle?.let { this.handle = it }
        bio?.let { this.bio = it }
        profileImageUrl?.let { this.profileImageUrl = copyImageToInternalStorage(it) ?: it }
        prefs
            ?.edit()
            ?.apply {
                putString(KEY_NICKNAME, this@SampleProfileStore.nickname)
                putString(KEY_HANDLE, this@SampleProfileStore.handle)
                putString(KEY_BIO, this@SampleProfileStore.bio)
                putString(KEY_IMAGE, this@SampleProfileStore.profileImageUrl)
            }?.apply()
        Log.d(TAG, "저장: nickname=${this.nickname} handle=${this.handle} image=${this.profileImageUrl}")
    }

    /** 더미 프로필에 그동안의 생성·수정 결과를 덮어씌운다. 비활성이거나 프로필이 없으면 그대로 돌려준다. */
    fun applyTo(profile: MyProfileVO?): MyProfileVO? {
        if (!isActive || profile == null) return profile
        return profile.copy(
            nickname = nickname ?: profile.nickname,
            handle = handle ?: profile.handle,
            bio = bio ?: profile.bio,
            profileImageUrl = profileImageUrl ?: profile.profileImageUrl,
        )
    }

    /**
     * 고른 이미지를 앱 내부 저장소로 복사하고 `file://` 경로를 돌려준다.
     *
     * 포토피커가 주는 `content://` URI 는 일회성 읽기 권한이라 프로세스가 죽으면 못 읽는다
     * (포토피커 URI 는 takePersistableUriPermission 도 받지 못한다). 내부 저장소로 복사해 두면
     * 권한 없이 계속 읽을 수 있다. 실패하면 null 을 돌려 원본 URI 를 그대로 쓴다(최소한 이번 세션엔 보인다).
     *
     * 더미 전용 경로라 클릭 스레드에서 동기 복사한다(이미지 1장, 1회성).
     *
     * 파일명은 매번 새로 만든다. 고정 파일명이면 두 번째로 고른 사진이 같은 경로를 덮어써 URL 문자열이
     * 그대로라, Coil 이 model 문자열을 캐시 키로 쓰는 탓에 이전 비트맵을 다시 그린다.
     */
    private fun copyImageToInternalStorage(uri: String): String? {
        val context = appContext
        return when {
            // 이미 복사해 둔 경로면 다시 복사하지 않는다(같은 파일 읽기/쓰기 방지).
            uri.startsWith("file://") -> {
                uri
            }

            context == null -> {
                null
            }

            else -> {
                runCatching {
                    val file = File(context.filesDir, "$IMAGE_FILE_PREFIX${System.currentTimeMillis()}")
                    context.contentResolver.openInputStream(Uri.parse(uri))?.use { input ->
                        file.outputStream().use(input::copyTo)
                    } ?: error("이미지를 읽지 못했습니다: $uri")
                    // 복사에 성공한 뒤에만 이전 사본을 지운다(실패 시 기존 사진을 잃지 않게).
                    deleteCopiedImages(context, keep = file.name)
                    Uri.fromFile(file).toString()
                }.onFailure { Log.w(TAG, "프로필 이미지 복사 실패", it) }
                    .getOrNull()
            }
        }
    }

    /** 내부 저장소에 쌓인 프로필 사진 사본을 지운다. [keep] 과 이름이 같은 파일은 남긴다. */
    private fun deleteCopiedImages(
        context: Context,
        keep: String? = null,
    ) {
        context.filesDir
            .listFiles { file -> file.name.startsWith(IMAGE_FILE_PREFIX) && file.name != keep }
            ?.forEach { it.delete() }
    }

    fun clear() {
        nickname = null
        handle = null
        bio = null
        profileImageUrl = null
        prefs?.edit()?.clear()?.apply()
        appContext?.let { runCatching { deleteCopiedImages(it) } }
    }

    private const val KEY_NICKNAME = "nickname"
    private const val KEY_HANDLE = "handle"
    private const val KEY_BIO = "bio"
    private const val KEY_IMAGE = "profile_image_url"
    private const val IMAGE_FILE_PREFIX = "sample_profile_image_"
}
