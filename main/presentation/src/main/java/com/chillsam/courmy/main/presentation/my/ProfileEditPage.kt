package com.chillsam.courmy.main.presentation.my

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.my.InterestRegionPage
import com.chillsam.courmy.main.domain.my.InterestThemePage
import com.chillsam.courmy.main.presentation.component.BackTopBar

/**
 * 프로필 편집 화면(FS-26). 아바타·닉네임·아이디·관심 테마/지역을 편집한다.
 *
 * 저장은 [ProfileEditViewModel] 이 `PATCH /api/v1/users` 로 처리하며, 사진을 새로 골랐으면
 * 프리사인 업로드를 먼저 끝낸다.
 *
 * TODO-API-SPEC: Figma 의 "소개"(bio) 입력은 서버 `UpdateProfileRequest`·마이페이지 응답 어디에도
 * 필드가 없어 저장할 방법이 없다. 저장되지 않는 입력을 노출하지 않으려 지금은 렌더하지 않는다.
 * 서버에 bio 가 생기면 입력과 매핑을 함께 되살린다. [wiki-needed]
 */
@Composable
fun ProfileEditPage(
    modifier: Modifier = Modifier,
    viewModel: ProfileEditViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 저장이 끝나면 이전 화면으로. 마이 화면은 resume 시 프로필을 다시 불러 갱신된 값을 보여준다.
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) navigationHelper.navigateToBack()
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(ProfileEditIntent.ConsumeError)
        }
    }

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
                imageUri = uiState.displayImageUrl,
                onImagePicked = { viewModel.onIntent(ProfileEditIntent.ImagePicked(it.toString())) },
                modifier = Modifier.padding(top = 28.dp, bottom = 12.dp),
            )

            LabeledField(
                label = "닉네임",
                value = uiState.nickname,
                onValueChange = { viewModel.onIntent(ProfileEditIntent.NicknameChanged(it)) },
                counter = "${uiState.nickname.length}/12",
            )
            LabeledField(
                label = "아이디",
                value = uiState.handle,
                onValueChange = { viewModel.onIntent(ProfileEditIntent.HandleChanged(it)) },
                inputTrailing = {
                    CheckButton(
                        enabled = uiState.handleChanged && !uiState.isCheckingHandle,
                        onClick = { viewModel.onIntent(ProfileEditIntent.CheckHandle) },
                    )
                },
                belowField = {
                    uiState.handleCheck?.let { result ->
                        DsText(
                            text = result.message,
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = if (result.available) color.contentSuccess else color.contentDanger,
                            modifier = Modifier.padding(start = 16.dp, top = 6.dp),
                        )
                    }
                },
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
                text = if (uiState.isSaving) "저장 중…" else "변경 사항 저장",
                enabled = uiState.hasChanges && !uiState.isSaving,
                onClick = { viewModel.onIntent(ProfileEditIntent.Save) },
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
