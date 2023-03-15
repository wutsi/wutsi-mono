package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class Timeout(
    val url: String,
    val delay: Int,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Timeout,
        attributes = mapOf(
            "url" to url,
            "delay" to delay,
        ),
    )
}
