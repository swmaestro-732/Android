package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO

/**
 * 코스 상세 화면(FS-11). [courseId] 로 서버에서 상세를 조회해 커버·제목·테마·요약·작성자·장소를 노출한다.
 */
@Composable
fun CourseDetailPage(
    courseId: String,
    viewModel: CourseDetailViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(courseId) { viewModel.load(courseId) }

    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0),
    ) {
        val course = uiState.course
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = color.contentAccent,
                )
            }

            uiState.isError || course == null -> {
                DsText(
                    text = "코스를 불러오지 못했어요",
                    modifier = Modifier.align(Alignment.Center),
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel2,
                )
            }

            else -> {
                CourseDetailContent(course = course)
            }
        }
    }
}

@Composable
private fun CourseDetailContent(course: CourseDetailVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
    ) {
        if (course.coverImageUrl.isNotBlank()) {
            AsyncImage(
                model = course.coverImageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f)
                        .background(color.borderDefaultLevel0),
                contentScale = ContentScale.Crop,
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ThemeChips(themes = course.themes)
            DsText(
                text = course.title,
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
            )
            StatsRow(course = course)
            AuthorRow(course = course)
            if (course.description.isNotBlank()) {
                DsText(
                    text = course.description,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel1,
                )
            }
            DsText(
                text = "코스 동선",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
                modifier = Modifier.padding(top = 8.dp),
            )
            course.places.forEachIndexed { index, place ->
                PlaceItem(order = index + 1, place = place)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeChips(themes: List<String>) {
    if (themes.isEmpty()) return
    val color = DesignSystemThemeImpl.designSystemColor
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        themes.forEach { theme ->
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(color.bgAccentSubtle)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                DsText(
                    text = "#$theme",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentAccent,
                )
            }
        }
    }
}

@Composable
private fun StatsRow(course: CourseDetailVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    val parts =
        buildList {
            add("장소 ${course.placeCount}곳")
            if (course.walkingMinutes > 0) add("도보 ${course.walkingMinutes}분")
            if (course.tracingCountLabel.isNotBlank()) add("${course.tracingCountLabel} 따라감")
        }
    DsText(
        text = parts.joinToString(" · "),
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color = color.contentDefaultLevel2,
    )
}

@Composable
private fun AuthorRow(course: CourseDetailVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    val author = course.author
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            model = author.profileImageUrl,
            contentDescription = null,
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.borderDefaultLevel0),
            contentScale = ContentScale.Crop,
        )
        DsText(
            text = author.nickname,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
        if (author.handle.isNotBlank()) {
            DsText(
                text = "@${author.handle}",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
        }
    }
}

@Composable
private fun PlaceItem(
    order: Int,
    place: CourseDetailPlaceVO,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DsText(
                text = "$order",
                modifier =
                    Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.contentDefaultLevel0)
                        .padding(top = 1.dp),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentOnAccent,
            )
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            if (place.categories.isNotEmpty()) {
                DsText(
                    text = place.categories.joinToString(" · "),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
            }
        }
        if (place.imageUrls.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                place.imageUrls.take(3).forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color.borderDefaultLevel0),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
        if (place.caption.isNotBlank()) {
            DsText(
                text = "✎ ${place.caption}",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentAccent,
            )
        }
        place.walkingMinutesToNext?.let { minutes ->
            DsText(
                text = "＋ 도보 ${minutes}분",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
                modifier = Modifier.padding(vertical = 2.dp),
            )
        }
    }
}
