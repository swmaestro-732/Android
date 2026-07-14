package com.chillsam.courmy.common.presentation.ui.typo

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.chillsam.courmy.common.presentation.R

internal val pretendardTextFont =
    FontFamily(
        Font(R.font.pretendard_bold, weight = FontWeight.Bold),
        Font(R.font.pretendard_extrabold, weight = FontWeight.ExtraBold),
        Font(R.font.pretendard_medium),
        Font(R.font.pretendard_semibold, weight = FontWeight.SemiBold),
    )
