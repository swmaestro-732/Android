package com.chillsam.courmy.main.presentation.my

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.my.InterestRegionPage
import com.chillsam.courmy.main.domain.my.InterestThemePage
import com.chillsam.courmy.main.presentation.component.BackTopBar

/** 프로필 편집 화면(FS-26). 아바타·닉네임·아이디·소개·관심 테마/지역을 편집한다. */
@Composable
fun ProfileEditPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    var nickname by remember { mutableStateOf("홍지호") }
    var handle by remember { mutableStateOf("jiho_routes") }
    var bio by remember { mutableStateOf("성수동 구석구석 카페 탐험가 · 걷기 좋은 코스를 만들어 나눠요") }

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
            AvatarEditor(modifier = Modifier.padding(vertical = 12.dp))

            LabeledField(
                label = "닉네임",
                value = nickname,
                onValueChange = { nickname = it },
                counter = "${nickname.length}/12",
            )
            LabeledField(
                label = "아이디",
                value = handle,
                onValueChange = { handle = it },
                trailing = {
                    DsText(
                        text = "✓ 사용 가능",
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentAccent,
                    )
                },
                inputTrailing = { CheckButton(onClick = {}) },
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
                onClick = { navigationHelper.navigateToBack() },
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
private fun AvatarEditor(modifier: Modifier = Modifier) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(100.dp)) {
            // 아바타 원(자식으로 두면 clip 에 배지가 잘리므로 배지와 형제로 분리).
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
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(color.imagePlaceholder),
            )
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color.bgAccent)
                        .clickable {},
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
    }
}

/** 아이디 중복 확인 버튼(컴팩트, Secondary 스타일). 실동작은 아이디 API 연동 때 연결. */
@Composable
private fun CheckButton(onClick: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.bgAccentSubtle)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = "중복 확인",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentAccent,
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
                if (region) {
                    RegionChip(text = chip)
                } else {
                    DsChip(text = chip, selected = true)
                }
            }
        }
    }
}

/** 관심 지역 요약 칩: 옅은 강조 배경 + 위치 아이콘 + 강조 텍스트(FS-26). */
@Composable
private fun RegionChip(text: String) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .clip(CircleShape)
                .background(color.bgAccentSubtle)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_location_24),
            contentDescription = null,
            tint = color.contentAccent,
            modifier = Modifier.size(14.dp),
        )
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentAccent,
        )
    }
}
