package com.chillsam.courmy.course.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsRatingInput
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.entity.CourseReviewLimits
import com.chillsam.courmy.course.entity.CourseReviewTagCatalog
import com.chillsam.courmy.course.entity.CourseReviewTagVO
import com.chillsam.courmy.course.entity.PlaceReviewTagCatalog
import com.chillsam.courmy.course.presentation.component.CoursePhotoRow

/** 별점을 고르기 전 보여줄 안내. 고르고 나면 점수 문구로 바뀐다. */
private val RATING_LABELS =
    listOf("별로였어요", "아쉬웠어요", "보통이었어요", "좋았어요", "최고였어요")

/**
 * 후기 작성 화면.
 *
 * 별점만 필수고 본문·사진·태그는 선택이다(서버 검증과 같다). 코스 작성 화면의 사진 행
 * ([CoursePhotoRow])과 공용 칩·버튼을 그대로 써서 입력 관례를 맞춘다.
 *
 * 화면을 닫는 시점은 호출부가 정한다 — 제출이 끝나면 목록을 다시 받아야 하므로
 * 성공 처리를 여기서 하지 않는다.
 */
@Composable
internal fun CourseReviewWriteScreen(
    state: CourseReviewWriteUIState,
    onIntent: (CourseReviewWriteIntent) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    tags: List<CourseReviewTagVO> = CourseReviewTagCatalog.entries.map { it.toTagVO() },
    isPlace: Boolean = false,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    // 이 화면은 목록 라우트 위에 덮여 있어서, 막지 않으면 뒤로 가기가 목록째로 닫아 버린다.
    // 제출이 나가 있는 동안은 닫지 않는다 — 돌아올 응답을 받을 화면이 사라진다.
    BackHandler { if (!state.isSubmitting) onClose() }
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0)
                .imePadding(),
    ) {
        ReviewWriteTopBar(onClose = { if (!state.isSubmitting) onClose() })
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            RatingField(
                title = if (isPlace) "이 장소 어땠나요?" else "이 코스 어땠나요?",
                rating = state.rating,
                enabled = !state.isSubmitting,
                onRatingChange = { onIntent(CourseReviewWriteIntent.ChangeRating(it)) },
            )
            ContentField(
                placeholder = if (isPlace) "장소를 방문한 경험을 적어 주세요. (선택)" else "코스를 따라간 경험을 적어 주세요. (선택)",
                content = state.content,
                enabled = !state.isSubmitting,
                onContentChange = { onIntent(CourseReviewWriteIntent.ChangeContent(it)) },
            )
            PhotoField(
                photoUris = state.photoUris,
                onPhotosChange = { onIntent(CourseReviewWriteIntent.ChangePhotos(it)) },
            )
            TagField(
                tags = tags,
                isPlace = isPlace,
                enabled = !state.isSubmitting,
                selected = state.selectedTagCodes,
                canSelectMore = state.canSelectMoreTags,
                onToggle = { onIntent(CourseReviewWriteIntent.ToggleTag(it)) },
            )
            if (state.errorMessage != null) {
                DsText(
                    text = state.errorMessage,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDanger,
                    maxLines = Int.MAX_VALUE,
                )
            }
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(color.bgDefaultLevel1)
                    .navigationBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
        ) {
            DsButton(
                text = "후기 등록",
                onClick = onSubmit,
                enabled = state.canSubmit,
                loading = state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * 상단 바: ✕ 닫기 + "후기 쓰기".
 *
 * 코스 작성의 [com.chillsam.courmy.course.presentation.component.CourseTopBar] 를 쓰지 않는다 —
 * 제목이 "새 코스 만들기" 로 박혀 있고, 후기에는 없는 임시저장 자리가 함께 붙는다.
 */
@Composable
private fun ReviewWriteTopBar(onClose: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel1)
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconButton(onClick = onClose, modifier = Modifier.size(48.dp)) {
            Icon(
                painter = painterResource(R.drawable.close_small_24),
                contentDescription = "후기 작성 닫기",
                tint = color.contentDefaultLevel0,
                modifier = Modifier.size(24.dp),
            )
        }
        DsText(
            text = "후기 쓰기",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
    }
}

/** 별점(필수). 고른 점수에 맞는 문구를 옆에 세워 선택이 됐는지 분명히 한다. */
@Composable
private fun RatingField(
    title: String,
    rating: Int,
    enabled: Boolean,
    onRatingChange: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(title = title, required = true)
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DsRatingInput(rating = rating, onRatingChange = onRatingChange, enabled = enabled)
            DsText(
                text = RATING_LABELS.getOrNull(rating - 1) ?: "별점을 골라 주세요",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
            )
        }
    }
}

/** 본문(선택). 남은 글자 수를 세어 상한에 닿기 전에 알 수 있게 한다. */
@Composable
private fun ContentField(
    placeholder: String,
    content: String,
    enabled: Boolean,
    onContentChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(title = "방문 경험을 들려주세요")
        DsTextField(
            value = content,
            onValueChange = onContentChange,
            enabled = enabled,
            placeholder = placeholder,
            // 후기는 여러 줄로 쓰는 글이라 한 줄로 가두지 않는다.
            singleLine = false,
            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
        )
        DsText(
            text = "${content.length}/${CourseReviewLimits.MAX_CONTENT_LENGTH}",
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
        )
    }
}

/** 사진(선택). 업로드는 제출할 때 한 번에 한다. */
@Composable
private fun PhotoField(
    photoUris: List<String>,
    onPhotosChange: (List<String>) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(title = "사진을 더해 주세요")
        CoursePhotoRow(
            photos = photoUris,
            maxPhotos = CourseReviewLimits.MAX_PHOTOS,
            onPhotosChange = onPhotosChange,
        )
    }
}

/** 태그(선택, 최대 [CourseReviewLimits.MAX_TAGS] 개). 서버 enum 을 그대로 옮긴 카탈로그에서 고른다. */
@Composable
private fun TagField(
    tags: List<CourseReviewTagVO>,
    isPlace: Boolean,
    enabled: Boolean,
    selected: Set<String>,
    canSelectMore: Boolean,
    onToggle: (String) -> Unit,
) {
    val groups =
        remember(tags, isPlace) {
            tags.groupBy { tag ->
                if (isPlace) {
                    PlaceReviewTagCatalog.entries.firstOrNull { it.code == tag.code }?.group
                } else {
                    CourseReviewTagCatalog.entries.firstOrNull { it.code == tag.code }?.group
                }.orEmpty().ifBlank { "기타" }
            }
        }
    var activeGroup by rememberSaveable(isPlace) { mutableStateOf(groups.keys.firstOrNull().orEmpty()) }
    val currentGroup = activeGroup.takeIf { it in groups } ?: groups.keys.firstOrNull().orEmpty()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title = "키워드로 표현해 주세요")
        DsText(
            text = "좋았던 점을 최대 ${CourseReviewLimits.MAX_TAGS}개 골라 주세요. (선택)",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            groups.keys.forEach { title ->
                DsChip(text = title, selected = title == currentGroup, onClick = { activeGroup = title })
            }
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            groups[currentGroup].orEmpty().forEach { tag ->
                val isSelected = tag.code in selected
                DsChip(
                    text = "${tag.icon} ${tag.label}",
                    selected = isSelected,
                    enabled = enabled && (isSelected || canSelectMore),
                    onClick = { onToggle(tag.code) },
                )
            }
        }
        SelectedReviewKeywords(tags.filter { it.code in selected }, enabled, onToggle)
    }
}

@Composable
private fun SelectedReviewKeywords(
    tags: List<CourseReviewTagVO>,
    enabled: Boolean,
    onRemove: (String) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        DsText(
            text = "선택한 키워드 ${tags.size}/${CourseReviewLimits.MAX_TAGS}",
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
        if (tags.isEmpty()) {
            DsText(
                text = "선택한 키워드가 여기에 모여요",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                tags.forEach { tag ->
                    DsChip(
                        text = "${tag.icon} ${tag.label} ×",
                        modifier = Modifier.semantics { contentDescription = "${tag.label} 선택 해제" },
                        selected = true,
                        enabled = enabled,
                        onClick = { onRemove(tag.code) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    required: Boolean = false,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        DsText(
            text = title,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
        if (required) {
            DsText(
                text = "*",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = DesignSystemThemeImpl.designSystemColor.contentAccent,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CourseReviewWritePreview() {
    DesignSystemTheme {
        CourseReviewWriteScreen(
            state = CourseReviewWriteUIState(rating = 4),
            onIntent = {},
            onSubmit = {},
            onClose = {},
        )
    }
}
