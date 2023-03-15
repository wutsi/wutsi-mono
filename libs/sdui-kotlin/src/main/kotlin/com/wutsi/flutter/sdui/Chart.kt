package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class Chart(
    val title: String? = null,
    val series: List<List<ChartData>> = emptyList(),
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Chart,
        attributes = mapOf(
            "title" to title,
            "series" to series,
        ),
    )
}

data class ChartData(
    val x: String?,
    val y: Double?,
)
