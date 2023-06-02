package com.wutsi.blog.app.common.model.tui

data class BarChartModel(
    val categories: List<String>,
    val series: List<BarChartSerieModel>,
)
