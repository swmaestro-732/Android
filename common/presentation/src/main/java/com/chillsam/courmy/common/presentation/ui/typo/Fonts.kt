package com.chillsam.courmy.common.presentation.ui.typo

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.chillsam.courmy.common.presentation.R

internal val pretendardTextFont =
    FontFamily(
        Font(R.font.pretendard_bold, weight = FontWeight.Bold),
        Font(R.font.pretendard_regular),
        Font(R.font.pretendard_semibold, weight = FontWeight.SemiBold),
    )
