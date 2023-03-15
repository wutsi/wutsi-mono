package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class Html(
    val data: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Html,
        attributes = mapOf(
            "data" to data,
        ),
    )
}
