package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.Icon

class Icon(
    val code: String,
    val size: Double? = null,
    val color: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Icon,
        attributes = mapOf(
            "code" to code,
            "size" to size,
            "color" to color,
        ),
    )
}
