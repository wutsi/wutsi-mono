package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class DynamicWidget(
    val url: String,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.DynamicWidget,
        attributes = mapOf(
            "url" to url,
        ),
    )
}
