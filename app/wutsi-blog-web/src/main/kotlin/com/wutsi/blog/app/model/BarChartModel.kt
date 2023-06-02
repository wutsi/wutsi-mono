package com.wutsi.blog.app.model

data class BarChartModel(
    val categories: List<String>,
    val series: List<BarChartSerieModel>,
)
