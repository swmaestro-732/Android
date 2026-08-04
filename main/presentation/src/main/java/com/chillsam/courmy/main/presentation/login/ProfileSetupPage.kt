package com.chillsam.courmy.main.presentation.login

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.login.SignupThemePage
import com.chillsam.courmy.main.presentation.component.SignupProgressBar
import com.chillsam.courmy.main.presentation.profile.SampleProfileStore

/** 프로필·아이디 설정 화면(FS-05). 아바타·닉네임·아이디를 정하고 다음(완료)으로 이어진다. */
@Composable
fun ProfileSetupPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    var nickname by remember { mutableStateOf("") }
    var handle by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var idResult by remember { mutableStateOf<IdCheckResult?>(null) }
    // 다음은 닉네임 입력 + 아이디 중복 확인에서 "사용 가능"을 받은 경우에만 활성.
    val canContinue = nickname.isNotBlank() && idResult?.available == true

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel1).statusBarsPadding()) {
        SignupProgressBar(step = 1, modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp))
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            DsText(
                text = "프로필을\n만들어 주세요",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                maxLines = 2,
                modifier = Modifier.padding(top = 24.dp, bottom = 24.dp),
            )
            AvatarEditor(
                imageUri = profileImageUri,
                onImagePicked = { profileImageUri = it },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(28.dp))

            FieldLabel("닉네임")
            DsTextField(
                value = nickname,
                onValueChange = { nickname = it },
                placeholder = "닉네임을 입력해 주세요",
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )

            Spacer(Modifier.height(20.dp))
            FieldLabel("아이디")
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DsTextField(
                    value = handle,
                    onValueChange = {
                        handle = it
                        idResult = null // 값이 바뀌면 이전 확인 결과 무효화.
                    },
                    placeholder = "아이디",
                    // 중복 확인 실패 시 위험(빨간 테두리) 표시.
                    isError = idResult?.available == false,
                    // "@"는 항상 붙는 기본값이라 입력창 앞 고정 프리픽스로 표시.
                    leadingIcon = {
                        DsText(
                            text = "@",
                            style = DesignSystemThemeImpl.typeScale.textRegularS,
                            color = color.contentDefaultLevel2,
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
                CheckButton(enabled = handle.isNotBlank(), onClick = { idResult = checkHandle(handle) })
            }
            val result = idResult
            if (result != null) {
                DsText(
                    text = result.message,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = if (result.available) color.contentSuccess else color.contentDanger,
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp),
                )
            } else {
                // 확인 전엔 아이디 조건을 줄마다 캡션으로 안내.
                Column(
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    DsText(
                        text = "영문 소문자, 숫자, 밑줄(_)만 사용 가능",
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel3,
                    )
                    DsText(
                        text = "3~12자 이내로 입력 가능",
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel3,
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        Box(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
            DsButton(
                text = "다음",
                enabled = canContinue,
                onClick = {
                    // 완료 화면의 회원가입 API 호출에 쓰도록 프로필을 홀더에 담는다.
                    SignupSelectionStore.nickname = nickname
                    SignupSelectionStore.handle = handle
                    SignupSelectionStore.profileImageUrl = profileImageUri?.toString()
                    // 더미 모드에서 가입 결과가 마이 화면에 보이도록 남긴다(실 연동 시 무영향).
                    SampleProfileStore.update(
                        nickname = nickname,
                        handle = handle,
                        profileImageUrl = profileImageUri?.toString(),
                    )
                    navigationHelper.navigateTo(SignupThemePage)
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textStrongS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1,
    )
}

/** 아바타 편집(카메라 배지 → 시스템 포토 피커로 갤러리 이미지 선택, Coil 로 렌더). */
@Composable
private fun AvatarEditor(
    imageUri: Uri?,
    onImagePicked: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val pickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) onImagePicked(uri)
        }
    Box(modifier = modifier.size(100.dp)) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(color.imagePlaceholder),
            contentAlignment = Alignment.Center,
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "프로필 이미지",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_tab_person_24),
                    contentDescription = null,
                    tint = color.contentOnAccent,
                    modifier = Modifier.size(40.dp),
                )
            }
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color.bgAccent)
                    .clickable {
                        pickImage.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_camera_24),
                contentDescription = "사진 추가",
                tint = color.contentOnAccent,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

private val TAKEN_HANDLES = setOf("admin", "test", "courmy", "jiho")
private val HANDLE_REGEX = Regex("^[a-z0-9_]+$")

/** 아이디 검증 결과(사유 포함). 실 판정은 아이디 API·검증 스펙 확정 후. */
private data class IdCheckResult(
    val message: String,
    val available: Boolean,
)

/** 목 검증: 길이 → 형식 → 중복 순으로 첫 실패 사유 반환(실 API 전 시연용). */
private fun checkHandle(handle: String): IdCheckResult =
    when {
        handle.length !in 3..12 -> IdCheckResult("3~12자 이내로 입력해 주세요", available = false)
        !handle.matches(HANDLE_REGEX) -> IdCheckResult("영문 소문자, 숫자, 밑줄(_)만 사용할 수 있어요", available = false)
        handle in TAKEN_HANDLES -> IdCheckResult("이미 사용 중인 아이디예요", available = false)
        else -> IdCheckResult("사용할 수 있는 아이디예요", available = true)
    }

/** 아이디 중복 확인 버튼(컴팩트). 활성=강조 배경, 비활성=흰 배경+회색 테두리. */
@Composable
private fun CheckButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier =
            Modifier
                .height(46.dp)
                .clip(shape)
                .background(if (enabled) color.bgAccent else color.bgDefaultLevel1)
                .then(
                    if (enabled) Modifier else Modifier.border(1.dp, color.borderDefaultLevel1, shape),
                ).clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = "중복 확인",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = if (enabled) color.contentOnAccent else color.contentDefaultLevel2,
        )
    }
}
