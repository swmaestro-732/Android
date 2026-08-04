package com.chillsam.courmy.main.domain.auth

import com.chillsam.courmy.common.domain.media.MediaRepository
import com.chillsam.courmy.main.domain.profile.ProfileRepository
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
    ) {
        /**
         * @param localImageUri 사용자가 고른 로컬 이미지(content://). 없으면 null.
         * @return 이미지까지 정상 반영됐으면 true. 가입만 성공하고 이미지가 실패했으면 false.
         */
        suspend operator fun invoke(
            profile: SignupProfile,
            localImageUri: String? = null,
        ): Boolean {
            repository.signup(profile)
            if (localImageUri.isNullOrBlank()) return true

            return runCatching {
                val imageUrl = mediaRepository.uploadImage(localImageUri)
                profileRepository.updateProfile(profileImageUrl = imageUrl)
            }.onFailure { if (it is CancellationException) throw it }
                .isSuccess
        }
    }
