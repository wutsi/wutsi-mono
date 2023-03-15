package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class Tab(
    val icon: String,
    val caption: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Tab,
        attributes = mapOf(
            "icon" to icon,
            "caption" to caption,
        ),
    )
}
