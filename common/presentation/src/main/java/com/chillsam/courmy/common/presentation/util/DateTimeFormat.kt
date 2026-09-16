package com.chillsam.courmy.common.presentation.util

import java.text.SimpleDateFormat
import java.util.Locale

/** 저장 시각을 "yyyy.MM.dd HH:mm" 로 포맷. minSdk 24 를 고려해 java.time 대신 SimpleDateFormat 사용. */
fun formatCreatedAt(millis: Long): String = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA).format(millis)
