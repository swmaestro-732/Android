package com.chillsam.courmy.main.domain.profile

import com.chillsam.courmy.common.domain.media.MediaRepository
import javax.inject.Inject

/**
 * 내 프로필 수정. 바뀐 항목만 넘기고, null 인 항목은 서버에서 건드리지 않는다.
 *
 * 새 이미지를 골랐으면 presign 업로드로 공개 URL 을 먼저 확보한 뒤 그 URL 로 프로필을 갱신한다
 * (업로드가 실패하면 프로필도 갱신하지 않는다 — 반쯤 반영된 상태를 만들지 않기 위해).
 */
class UpdateProfileUseCase
    @Inject
    constructor(
        private val profileRepository: ProfileRepository,
        private val mediaRepository: MediaRepository,
    ) {
        suspend operator fun invoke(
            nickname: String? = null,
            handle: String? = null,
            localImageUri: String? = null,
            bio: String? = null,
        ) {
            val imageUrl =
                localImageUri
                    ?.takeIf { it.isNotBlank() }
                    ?.let { mediaRepository.uploadImage(it) }
            // 바뀐 항목이 하나도 없으면 빈 요청을 보내지 않는다.
            if (listOfNotNull(nickname, handle, imageUrl, bio).isEmpty()) return
            profileRepository.updateProfile(
                nickname = nickname,
                handle = handle,
                profileImageUrl = imageUrl,
                bio = bio,
            )
        }
    }
