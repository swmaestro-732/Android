package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CourseStopVO

/**
 * 코스 완성(저장 성공) 화면(Figma FS-34-Done). 완료 체크·안내 → 코스 요약 카드 →
 * "코스 동선" 타임라인 → 하단 "내 코스에서 보기" 액션으로 구성한다. 표시 전용 화면.
 */
@Composable
fun CourseCompleteScreen(
    course: CourseCompleteVO,
    onClose: () -> Unit,
    onViewMyCourses: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0)
                .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.bgDefaultLevel1)
                        .clickable(onClick = onClose)
                        .clearAndSetSemantics {
                            contentDescription = "닫기"
                            role = Role.Button
                        },
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "✕",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel0,
                )
            }
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CompleteHeader()
            SummaryCard(course = course)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DsText(
                    text = "코스 동선",
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                )
                Column {
                    course.stops.forEachIndexed { index, stop ->
                        StopRow(
                            stop = stop,
                            connectAbove = index > 0,
                            connectBelow = index < course.stops.lastIndex,
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            DsButton(text = "내 코스에서 보기", onClick = onViewMyCourses)
        }
    }
}

/** 완료 체크 원 + 제목 + 안내 문구(가운데 정렬). */
@Composable
private fun CompleteHeader() {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape).background(color.bgAccent),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = "✓",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentOnAccent,
            )
        }
        DsText(
            text = "코스가 완성됐어요!",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = color.contentDefaultLevel0,
            textAlign = TextAlign.Center,
        )
        DsText(
            text = "내 코스에 저장되었어요.",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
            textAlign = TextAlign.Center,
        )
    }
}

/** 코스 요약 카드: 썸네일 + 카테고리 · 제목 · 규모. */
@Composable
private fun SummaryCard(course: CourseCompleteVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, color.borderDefaultLevel0, shape),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(color.imagePlaceholder),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(color.bgDefaultLevel0)
                    .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            DsText(
                text = course.category,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
            DsText(
                text = course.title,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = course.summaryText,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
    }
}

/** 코스 동선 1행: 번호(타임라인) · 썸네일 · 이름/카테고리·시간. */
@Composable
private fun StopRow(
    stop: CourseStopVO,
    connectAbove: Boolean,
    connectBelow: Boolean,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StopRail(number = stop.order, connectAbove = connectAbove, connectBelow = connectBelow)
        Row(
            modifier = Modifier.weight(1f).padding(start = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.imagePlaceholder),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                DsText(
                    text = stop.name,
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                )
                val subtitle =
                    if (stop.durationText.isBlank()) {
                        stop.category
                    } else {
                        "${stop.category} · ${stop.durationText}"
                    }
                DsText(
                    text = subtitle,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
            }
        }
    }
}

/** 타임라인 레일: 세로 연결선(위/아래 선택) 위에 번호 원을 중앙 배치. */
@Composable
private fun StopRail(
    number: Int,
    connectAbove: Boolean,
    connectBelow: Boolean,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier = Modifier.width(32.dp).fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        if (connectAbove) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .width(2.dp)
                        .fillMaxHeight(0.5f)
                        .background(color.bgAccent),
            )
        }
        if (connectBelow) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .width(2.dp)
                        .fillMaxHeight(0.5f)
                        .background(color.bgAccent),
            )
        }
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape).background(color.bgAccent),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = number.toString(),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentOnAccent,
            )
        }
    }
}

/**
 * FS-34-Done 디자인 확인용 예시 완성 데이터. 실제로는 저장된 코스 정보를 전달한다.
 */
fun sampleCourseComplete(title: String = "성수 종일 나들이 코스"): CourseCompleteVO =
    CourseCompleteVO(
        category = "성수 · 하루 코스",
        title = title,
        summaryText = "6 스팟 · 6시간",
        stops =
            listOf(
                CourseStopVO(1, "어니언 성수", "카페 · 베이커리", "60분"),
                CourseStopVO(2, "대림창고 갤러리", "전시 · 카페", "70분"),
                CourseStopVO(3, "센터커피 로스터리", "카페 · 디저트", "50분"),
                CourseStopVO(4, "서울숲 산책로", "공원 · 산책", "40분"),
                CourseStopVO(5, "글로우 서울", "디저트 · 포토존", "45분"),
                CourseStopVO(6, "소월길 와인바", "와인 · 다이닝", "90분"),
            ),
    )
