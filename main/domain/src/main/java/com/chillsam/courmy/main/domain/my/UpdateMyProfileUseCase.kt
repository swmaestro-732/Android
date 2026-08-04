package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.main.domain.profile.ProfileRepository
import com.chillsam.courmy.main.entity.my.MyProfileEditResultVO
import javax.inject.Inject

/**
 * 내 프로필 수정 UseCase(`PATCH /api/v1/users`).
 * null 인 인자는 "변경 안 함"이므로, 화면에서 바뀐 항목만 채워 넘긴다.
 *
 * 사진을 새로 골랐다면 서버가 URL 문자열만 받으므로 업로드를 **먼저** 끝내고
 * 그 결과 URL 로 수정을 요청한다(업로드 실패 시 수정 자체를 진행하지 않는다).
 */
class UpdateMyProfileUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) {
        suspend operator fun invoke(
            nickname: String? = null,
            handle: String? = null,
            localImageUri: String? = null,
        ): MyProfileEditResultVO {
            val uploadedImageUrl = localImageUri?.let { repository.uploadProfileImage(it) }
            return repository.updateProfile(
                nickname = nickname,
                handle = handle,
                profileImageUrl = uploadedImageUrl,
            )
        }
    }
