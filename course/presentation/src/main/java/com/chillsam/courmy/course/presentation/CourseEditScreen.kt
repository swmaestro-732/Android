package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.component.alignTextFieldBorder
import com.chillsam.courmy.common.presentation.ui.modifier.cardShadow
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.entity.CourseEditPlaceVO
import com.chillsam.courmy.course.presentation.component.CourseVisibilitySegment
import com.chillsam.courmy.course.presentation.component.SelectedTags
import com.chillsam.courmy.course.presentation.component.TagInputRow

/**
 * 코스 편집 화면.
 *
 * 바꿀 수 있는 건 코스 정보(제목·설명)·태그·장소별 한마디다. 사진과 장소 구성은 편집 대상이 아니라
 * 장소는 순번·이름만 읽기 전용으로 보여 주고 한마디 입력만 연다.
 *
 * 공개 설정은 서버가 PATCH 본문에 요구해서(빼면 400) 화면에 노출한다. 다만 상세 응답에 현재 값이 없어
 * PUBLIC 으로 시작하므로, 몰래 덮어쓰지 않도록 안내 문구를 함께 띄운다.
 */
@Composable
fun CourseEditScreen(
    state: CourseEditUIState,
    onIntent: (CourseEditIntent) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0),
    ) {
        EditTopBar(onClose = onClose)
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = color.contentAccent)
            }
            return@Column
        }
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            EditSection(title = "코스 정보") {
                EditFieldLabel("코스 이름")
                DsTextField(
                    value = state.title,
                    onValueChange = { onIntent(CourseEditIntent.ChangeTitle(it)) },
                    // 입력창은 포커스 링 자리로 좌우 3dp 를 비워 둔다. 그대로 두면 위 라벨보다
                    // 안쪽으로 들어가 보이므로 테두리를 라벨 선에 맞춘다.
                    modifier = Modifier.fillMaxWidth().alignTextFieldBorder(),
                    placeholder = "코스 이름을 입력해 주세요",
                    isError = state.title.isBlank(),
                )
                EditFieldLabel("코스 소개")
                DsTextField(
                    value = state.description,
                    onValueChange = { onIntent(CourseEditIntent.ChangeDescription(it)) },
                    modifier = Modifier.fillMaxWidth().alignTextFieldBorder(),
                    placeholder = "이 코스를 소개해 주세요",
                    singleLine = false,
                )
            }
            EditSection(title = "태그") {
                // 코스 생성의 태그 카드에서 선택 칩·입력행만 가져다 쓴다. 추천 태그는 생성 화면에서만
                // 받아 오는 값이라 편집에서는 두지 않는다.
                SelectedTags(
                    tags = state.tags,
                    onRemoveTag = { onIntent(CourseEditIntent.RemoveTag(it)) },
                )
                TagInputRow(onAddTag = { onIntent(CourseEditIntent.AddTag(it)) })
            }
            EditSection(title = "공개 설정") {
                // 현재 값을 못 읽었을 때만 알린다. 읽어 왔으면 그 값이 이미 선택돼 있으므로 조용히 둔다.
                if (!state.isVisibilityKnown) {
                    DsText(
                        text = "현재 설정을 불러오지 못했어요. 저장하면 아래에서 고른 값으로 바뀝니다.",
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel2,
                    )
                }
                CourseVisibilitySegment(
                    selected = state.visibility,
                    onSelect = { onIntent(CourseEditIntent.ChangeVisibility(it)) },
                )
            }
            EditSection(title = "장소별 한마디") {
                DsText(
                    text = "장소와 사진은 편집할 수 없어요. 한마디만 고칠 수 있어요.",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
                state.places.forEach { place ->
                    EditPlaceTipCard(
                        place = place,
                        onTipChange = { tip ->
                            onIntent(CourseEditIntent.ChangePlaceTip(place.placeId, tip))
                        },
                    )
                }
            }
        }
        EditSaveBar(
            enabled = state.canSave,
            isSaving = state.isSaving,
            onSave = { onIntent(CourseEditIntent.Save) },
        )
    }
}

/** 상단바: ✕ 닫기 + 화면 제목. 생성 흐름의 상단바와 달리 임시저장이 없다. */
@Composable
private fun EditTopBar(onClose: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.bgDefaultLevel1)
                    .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = "✕",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel0,
            )
        }
        DsText(
            text = "코스 편집",
            modifier = Modifier.weight(1f),
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = color.contentDefaultLevel0,
        )
    }
}

@Composable
private fun EditSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DsText(
            text = title,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
        content()
    }
}

@Composable
private fun EditFieldLabel(text: String) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
    )
}

/** 장소 1건: 순번 배지 + 이름(읽기 전용) + 한마디 입력. */
@Composable
private fun EditPlaceTipCard(
    place: CourseEditPlaceVO,
    onTipChange: (String) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .cardShadow(shape)
                .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color.bgAccent),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = (place.orderNo + 1).toString(),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentOnAccent,
                )
            }
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
            )
        }
        DsTextField(
            value = place.tip,
            onValueChange = onTipChange,
            modifier = Modifier.fillMaxWidth().alignTextFieldBorder(),
            placeholder = "이곳에 한마디를 남겨 보세요",
            singleLine = false,
        )
    }
}

/** 하단 고정 저장 바. 저장 중에는 눌리지 않고 문구로 진행을 알린다. */
@Composable
private fun EditSaveBar(
    enabled: Boolean,
    isSaving: Boolean,
    onSave: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = Modifier.background(color.bgDefaultLevel1)) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color.borderDefaultLevel0))
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(if (enabled) color.bgAccent else color.bgDefaultLevel0)
                    .then(if (enabled) Modifier.clickable(onClick = onSave) else Modifier),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            DsText(
                text = if (isSaving) "저장 중…" else "저장하기",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = if (enabled) color.contentOnAccent else color.contentDefaultLevel3,
            )
        }
    }
}
