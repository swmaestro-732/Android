package com.chillsam.courmy.main.presentation.my

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.shadow
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
import com.chillsam.courmy.main.domain.my.InterestRegionPage
import com.chillsam.courmy.main.domain.my.InterestThemePage
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.presentation.component.BackTopBar
import com.chillsam.courmy.main.presentation.profile.SampleProfileStore

/** 프로필 편집 화면(FS-26). 아바타·닉네임·아이디·소개·관심 테마/지역을 편집한다. */
@Composable
fun ProfileEditPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val profile = SampleProfileStore.applyTo(MyProfileVO.sample)
    val originalNickname = profile?.nickname ?: ORIGINAL_NICKNAME
    val originalHandle = profile?.handle ?: ORIGINAL_HANDLE
    val originalBio = profile?.bio ?: ORIGINAL_BIO
    var nickname by remember(originalNickname) { mutableStateOf(originalNickname) }
    var handle by remember(originalHandle) { mutableStateOf(originalHandle) }
    var idResult by remember { mutableStateOf<IdCheckResult?>(null) }
    var bio by remember(originalBio) { mutableStateOf(originalBio) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    val handleChanged = handle != originalHandle
    // 아이디를 바꿨다면 중복 확인에서 '사용 가능'을 받은 경우에만 저장 가능(검증 실패·미확인 시 비활성).
    val handleSaveable = !handleChanged || idResult?.available == true
    val hasChanges =
        handleSaveable &&
            (
                nickname != originalNickname ||
                    handleChanged ||
                    bio != originalBio ||
                    profileImageUri != null
            )

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        // 상단 바 영역은 흰색(Figma FS-26). 가운데 콘텐츠만 Gray200.
        Box(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
            BackTopBar(title = "프로필 편집", onBack = { navigationHelper.navigateToBack() })
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            AvatarEditor(
                // 새로 고른 게 없으면 현재 프로필 사진을 그대로 보여준다.
                imageUri = profileImageUri?.toString() ?: profile?.profileImageUrl.orEmpty(),
                onImagePicked = { profileImageUri = it },
                modifier = Modifier.padding(top = 28.dp, bottom = 12.dp),
            )

            LabeledField(
                label = "닉네임",
                value = nickname,
                onValueChange = { nickname = it },
                counter = "${nickname.length}/12",
            )
            LabeledField(
                label = "아이디",
                value = handle,
                onValueChange = {
                    handle = it
                    idResult = null // 값이 바뀌면 이전 검증 결과 무효화.
                },
                inputTrailing = {
                    CheckButton(
                        enabled = handle != originalHandle,
                        onClick = { idResult = checkHandle(handle) },
                    )
                },
                belowField = {
                    idResult?.let { result ->
                        DsText(
                            text = result.message,
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = if (result.available) color.contentSuccess else color.contentDanger,
                            modifier = Modifier.padding(start = 16.dp, top = 6.dp),
                        )
                    }
                },
            )
            LabeledField(
                label = "소개",
                value = bio,
                onValueChange = { bio = it },
                counter = "${bio.length}/60",
                singleLine = false,
            )

            InterestSummary(
                title = "관심 테마",
                chips = listOf("감성 카페", "전시·갤러리", "동네 산책"),
                onEdit = { navigationHelper.navigateTo(InterestThemePage) },
            )
            InterestSummary(
                title = "관심 지역",
                chips = listOf("성수", "연남", "한남"),
                onEdit = { navigationHelper.navigateTo(InterestRegionPage) },
                region = true,
            )
            Spacer(Modifier.height(12.dp))
        }

        // 하단 저장 버튼 영역도 흰색(Figma FS-26).
        Box(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
            DsButton(
                text = "변경 사항 저장",
                enabled = hasChanges,
                onClick = {
                    // 실 저장 API(PATCH 프로필)는 마이페이지 배포 후 연결한다.
                    // 그전까지는 더미 홀더에 남겨 편집 결과가 화면에 보이게 한다.
                    SampleProfileStore.update(
                        nickname = nickname,
                        handle = handle,
                        bio = bio,
                        profileImageUrl = profileImageUri?.toString(),
                    )
                    navigationHelper.navigateToBack()
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
private fun AvatarEditor(
    imageUri: String,
    onImagePicked: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val pickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) onImagePicked(uri)
        }
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(100.dp)) {
            // 아바타 원(흰 링 + 그림자). 배지는 형제로 분리해 clip 잘림 방지.
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 5.dp,
                            shape = CircleShape,
                            ambientColor = color.contentDefaultLevel0.copy(alpha = 0.12f),
                            spotColor = color.contentDefaultLevel0.copy(alpha = 0.16f),
                        ).clip(CircleShape)
                        .background(color.bgDefaultLevel1)
                        .padding(3.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(color.imagePlaceholder)) {
                    if (imageUri.isNotBlank()) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "프로필 이미지",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
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
                    contentDescription = "사진 변경",
                    tint = color.contentOnAccent,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    counter: String? = null,
    singleLine: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
    inputTrailing: (@Composable () -> Unit)? = null,
    belowField: (@Composable () -> Unit)? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = modifier.fillMaxWidth().padding(top = 20.dp)) {
        // 라벨·카운터를 필드 텍스트 들여쓰기(DsTextField 링3+좌우14≈16)에 맞춰 정렬.
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DsText(
                text = label,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
                modifier = Modifier.weight(1f),
            )
            counter?.let {
                DsText(
                    text = it,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                )
            }
            trailing?.invoke()
        }
        if (inputTrailing != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DsTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    singleLine = singleLine,
                )
                inputTrailing()
            }
        } else {
            DsTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = singleLine,
            )
        }
        belowField?.invoke()
    }
}

// 편집 원본값은 프로필 정본(MyProfileVO.sample) 한 곳에서만 가져온다(값 중복 방지).
private val ORIGINAL_HANDLE = MyProfileVO.sample.handle
private val ORIGINAL_NICKNAME = MyProfileVO.sample.nickname
private val ORIGINAL_BIO = MyProfileVO.sample.bio

/** 아이디 중복 확인 목 판정용 예약(사용 중) 아이디. 실 API 연동 시 제거. */
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
        handle.length !in 3..12 -> IdCheckResult("3~12자로 입력해 주세요", available = false)
        !handle.matches(HANDLE_REGEX) -> IdCheckResult("영문 소문자·숫자·_(밑줄)만 쓸 수 있어요", available = false)
        handle in TAKEN_HANDLES -> IdCheckResult("이미 사용 중인 아이디예요", available = false)
        else -> IdCheckResult("사용 가능한 아이디예요", available = true)
    }

/**
 * 아이디 중복 확인 액션 버튼(컴팩트). [enabled]=아이디가 원래값과 다를 때 활성.
 * 활성=강조 배경+on-accent, 비활성=흰 배경+회색 테두리·텍스트. 결과·사유는 필드 아래 텍스트로 표시.
 */
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
                    if (enabled) {
                        Modifier
                    } else {
                        Modifier.border(1.dp, color.borderDefaultLevel1, shape)
                    },
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InterestSummary(
    title: String,
    chips: List<String>,
    onEdit: () -> Unit,
    region: Boolean = false,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            DsText(
                text = title,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
                modifier = Modifier.weight(1f),
            )
            DsText(
                text = "편집",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentAccent,
                modifier = Modifier.clickable(onClick = onEdit),
            )
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            chips.forEach { chip ->
                OutlinedTagChip(
                    text = chip,
                    iconRes = if (region) R.drawable.ic_location_24 else null,
                )
            }
        }
    }
}

/** 관심 태그 칩: 흰 배경 + 강조 테두리 + 강조 텍스트(+ 선택적 아이콘). FS-26. */
@Composable
private fun OutlinedTagChip(
    text: String,
    iconRes: Int? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .clip(CircleShape)
                .background(color.bgDefaultLevel1)
                .border(1.dp, color.borderDefaultLevel1, CircleShape)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = color.contentAccent,
                modifier = Modifier.size(14.dp),
            )
        }
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentAccent,
        )
    }
}
