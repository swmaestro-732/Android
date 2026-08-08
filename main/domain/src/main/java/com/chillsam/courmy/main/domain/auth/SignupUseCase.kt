package com.chillsam.courmy.main.domain.auth

import com.chillsam.courmy.common.domain.media.MediaRepository
import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import com.chillsam.courmy.main.domain.profile.ProfileRepository
import com.chillsam.courmy.main.entity.area.AreaVO
import com.chillsam.courmy.main.entity.auth.SignupProfile
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * 회원가입 UseCase. 저장된 registrationToken + 입력 프로필로 가입을 완료한다.
 *
 * 프로필 이미지는 가입 **후에** 올린다. 업로드용 presign(`POST /api/v1/uploads/presign`)이
 * 액세스 토큰을 요구하는데, 가입 화면 시점엔 registrationToken 뿐이라 아직 계정이 없기 때문이다.
 * 가입이 성공하면 세션 토큰이 저장되므로, 그 직후 업로드 → 프로필 반영 순서로 이어붙인다.
 *
 * 이미지 업로드·반영 실패는 가입 자체를 되돌리지 않는다(계정은 이미 만들어졌다).
 * 사용자는 프로필 수정 화면에서 다시 시도할 수 있으므로, 여기서는 삼켜서 가입을 성공으로 끝낸다.
 */
class SignupUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
        private val mediaRepository: MediaRepository,
        private val profileRepository: ProfileRepository,
        private val telemetry: Telemetry,
    ) {
        /**
         * @param localImageUri 사용자가 고른 로컬 이미지(content://). 없으면 null.
         * @param interestThemes 가입 중 고른 관심 테마 이름.
         * @param interestRegions 가입 중 고른 관심 지역. 코드는 [profile] 로 서버에 실려 가고,
         *   이름까지 필요한 화면 표시용으로 기기에도 남긴다.
         * @return 이미지까지 정상 반영됐으면 true. 가입만 성공하고 이미지가 실패했으면 false.
         */
        suspend operator fun invoke(
            profile: SignupProfile,
            localImageUri: String? = null,
            interestThemes: List<String> = emptyList(),
            interestRegions: List<AreaVO> = emptyList(),
        ): Boolean =
            telemetry.track(AppFlow.Signup) {
                signup(profile, localImageUri, interestThemes, interestRegions)
            }

        private suspend fun signup(
            profile: SignupProfile,
            localImageUri: String?,
            interestThemes: List<String>,
            interestRegions: List<AreaVO>,
        ): Boolean {
            repository.signup(profile)
            // TODO-API-SPEC: 서버는 관심사를 받아 저장하지만 마이페이지 응답으로 돌려주지 않는다.
            //  화면이 되읽을 수 있게 기기에도 남긴다. 응답에 실리면 이 두 줄을 지운다. [wiki-needed]
            profileRepository.saveInterestThemes(interestThemes)
            profileRepository.saveInterestRegions(interestRegions)
            if (localImageUri.isNullOrBlank()) return true

            return runCatching {
                val imageUrl = mediaRepository.uploadImage(localImageUri)
                profileRepository.updateProfile(profileImageUrl = imageUrl)
            }.onFailure {
                if (it is CancellationException) throw it
                // 가입은 성공했지만 프로필 사진이 빠진 상태. 여기서 삼키면 운영에서는 흔적이 남지 않는다.
                telemetry.recordFailure(
                    AppFailure(area = AppFlow.Signup.eventName, kind = "profile_image_upload", cause = it),
                )
            }.isSuccess
        }
    }
