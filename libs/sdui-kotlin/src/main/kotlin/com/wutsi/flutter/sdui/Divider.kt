package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.Divider

class Divider(
    val height: Double? = null,
    val color: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Divider,
        attributes = mapOf(
            "height" to height,
            "color" to color,
        ),
    )
}
