package com.chillsam.courmy.fullScreenMedia.domain.tti

import com.chillsam.courmy.tti.TTIPage
import com.chillsam.courmy.tti.TimelineCategory

object FullScreenMediaTTIPage : TTIPage {
    override val pageName = "fullScreenMediaPage"
    override val timelines = listOf(
        TimelineCategory.TTI_TIME,
        TimelineCategory.API_REQUEST_READY_TIME,
        TimelineCategory.API_RESPONSE_TIME,
    )
}