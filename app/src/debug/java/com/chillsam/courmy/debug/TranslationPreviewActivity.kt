package com.chillsam.courmy.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

/**
 * ML Kit 온디바이스 번역 품질 확인용 PoC. 서버 콘텐츠·사용자 입력물을 흉내 낸 한국어 샘플을
 * 영어/일본어로 번역해 결과와 소요 시간을 보여 준다. 릴리스 APK 에는 포함되지 않는다.
 *
 * 실행:
 *   adb shell am start -n com.chillsam.courmy.debug/com.chillsam.courmy.debug.TranslationPreviewActivity
 *
 * 첫 번역 시 언어팩(~30MB)이 다운로드되므로 네트워크가 필요하다.
 */
class TranslationPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DesignSystemTheme {
                TranslationPreviewScreen()
            }
        }
    }
}

/** 번역 대상 언어. KOREAN 은 원문 그대로 보여 주는 비교 기준. */
private enum class TargetLang(
    val label: String,
    val mlkitCode: String?,
) {
    KOREAN("한국어(원문)", null),
    ENGLISH("English", TranslateLanguage.ENGLISH),
    JAPANESE("日本語", TranslateLanguage.JAPANESE),
}

private data class Sample(
    val kind: String,
    val text: String,
)

private val SAMPLES =
    listOf(
        Sample("리뷰", "분위기 진짜 좋고 사장님도 엄청 친절하세요. 디저트가 특히 맛있어서 또 올 것 같아요!"),
        Sample("리뷰(짧은)", "웨이팅 좀 있지만 기다릴 만함 ㅎㅎ"),
        Sample("장소 설명", "성수동 골목 안쪽에 자리한 아담한 로스터리 카페. 핸드드립과 시즌 한정 디저트를 선보인다."),
        Sample("코스 소개", "주말 오후, 한강을 따라 걷다가 노을 질 때쯤 분위기 좋은 와인바에서 마무리하는 데이트 코스예요."),
        Sample("해시태그", "#데이트코스 #성수감성 #분위기맛집"),
    )

@Composable
private fun TranslationPreviewScreen() {
    var target by remember { mutableStateOf(TargetLang.ENGLISH) }
    var status by remember { mutableStateOf("") }
    // index -> 번역 결과(또는 진행/오류 메시지)
    val results = remember { mutableStateMapOf<Int, String>() }

    fun translateAll(next: TargetLang) {
        target = next
        results.clear()
        val code = next.mlkitCode
        if (code == null) {
            status = "원문 표시"
            return
        }
        status = "언어팩 확인·번역 중…"
        val options =
            TranslatorOptions
                .Builder()
                .setSourceLanguage(TranslateLanguage.KOREAN)
                .setTargetLanguage(code)
                .build()
        val translator: Translator = Translation.getClient(options)
        val startedAt = System.currentTimeMillis()
        translator
            .downloadModelIfNeeded(DownloadConditions.Builder().build())
            .addOnSuccessListener {
                val downloadedAt = System.currentTimeMillis()
                SAMPLES.forEachIndexed { index, sample ->
                    results[index] = "번역 중…"
                    translator
                        .translate(sample.text)
                        .addOnSuccessListener { translated -> results[index] = translated }
                        .addOnFailureListener { e -> results[index] = "실패: ${e.message}" }
                }
                status = "언어팩 준비 ${downloadedAt - startedAt}ms · ${SAMPLES.size}건 번역 요청"
                // 샘플 번역이 모두 끝나면 translator 를 닫는다(간단 지연 없이 즉시 닫으면 진행 중 작업이 취소됨).
            }.addOnFailureListener { e ->
                status = "언어팩 다운로드 실패: ${e.message} (네트워크 확인)"
            }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        DsText(
            "ML Kit 온디바이스 번역 PoC",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
        Spacer(Modifier.height(4.dp))
        DsText(
            "한국어 샘플 → 선택 언어. 첫 실행은 언어팩 다운로드(~30MB)가 필요합니다.",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
            maxLines = 2,
        )
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TargetLang.entries.forEach { lang ->
                LangChip(
                    label = lang.label,
                    selected = lang == target,
                    onClick = { translateAll(lang) },
                )
            }
        }

        if (status.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            DsText(
                status,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = DesignSystemThemeImpl.designSystemColor.contentAccent,
                maxLines = 3,
            )
        }

        Spacer(Modifier.height(16.dp))
        SAMPLES.forEachIndexed { index, sample ->
            SampleCard(
                kind = sample.kind,
                original = sample.text,
                translated = results[index],
                showTranslation = target != TargetLang.KOREAN,
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun LangChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = DesignSystemThemeImpl.designSystemColor
    DsText(
        text = label,
        style = DesignSystemThemeImpl.typeScale.textStrongS,
        color = if (selected) colors.contentOnAccent else colors.contentDefaultLevel1,
        modifier =
            Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(if (selected) colors.bgAccent else colors.bgDefaultLevel1)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun SampleCard(
    kind: String,
    original: String,
    translated: String?,
    showTranslation: Boolean,
) {
    val colors = DesignSystemThemeImpl.designSystemColor
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, colors.borderDefaultLevel1, RoundedCornerShape(12.dp))
            .background(colors.bgDefaultLevel1)
            .padding(14.dp),
    ) {
        DsText(
            kind,
            style = DesignSystemThemeImpl.typeScale.textExtraXS,
            color = colors.contentAccent,
        )
        Spacer(Modifier.height(6.dp))
        DsText(
            original,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = colors.contentDefaultLevel0,
            maxLines = 5,
        )
        if (showTranslation) {
            Spacer(Modifier.height(10.dp))
            DsText(
                translated ?: "대기 중…",
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color =
                    if (translated == null) {
                        colors.contentDefaultLevel3
                    } else {
                        colors.contentDefaultLevel0
                    },
                maxLines = 6,
            )
        }
    }
}
