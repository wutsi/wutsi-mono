package com.wutsi.blog.app.common.model

import java.time.LocalDate

data class SurveyModel(
    val id: String = "",
    val url: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val active: Boolean = true,
)
