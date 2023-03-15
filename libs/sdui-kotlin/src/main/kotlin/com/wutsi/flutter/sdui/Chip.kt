package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class Chip(
    val color: String? = null,
    val backgroundColor: String? = null,
    val caption: String? = null,
    val shadowColor: String? = null,
    val padding: Double? = null,
    val elevation: Double? = null,
    val fontSize: Double? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Chip,
        attributes = mapOf(
            "id" to id,
            "color" to color,
            "padding" to padding,
            "elevation" to elevation,
            "shadowColor" to shadowColor,
            "backgroundColor" to backgroundColor,
            "caption" to caption,
            "fontSize" to fontSize,
        ),
    )
}
