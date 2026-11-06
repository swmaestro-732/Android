package com.chillsam.courmy.main.presentation.component

import androidx.annotation.DrawableRes
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.main.presentation.R

/** 홈과 관심 테마 선택에서 같은 코스 태그 일러스트를 사용한다. */
@get:DrawableRes
internal val CourseTagVO.themeImageRes: Int
    get() =
        when (this) {
            CourseTagVO.DATE -> R.drawable.img_theme_date
            CourseTagVO.HEALING -> R.drawable.img_theme_wellness
            CourseTagVO.FOOD -> R.drawable.img_theme_restaurant
            CourseTagVO.CAFETOUR -> R.drawable.img_theme_cafe
            CourseTagVO.CULTURE -> R.drawable.img_theme_culture
            CourseTagVO.NATURE -> R.drawable.img_theme_nature
            CourseTagVO.NIGHTVIEW -> R.drawable.img_theme_drinks
            CourseTagVO.SHOPPING -> R.drawable.img_theme_shopping
            CourseTagVO.TRADITION -> R.drawable.img_theme_landmark
            CourseTagVO.ACTIVITY -> R.drawable.img_theme_experience
            CourseTagVO.FAMILY -> R.drawable.img_theme_family
            CourseTagVO.SOLO -> R.drawable.img_theme_solo
        }
