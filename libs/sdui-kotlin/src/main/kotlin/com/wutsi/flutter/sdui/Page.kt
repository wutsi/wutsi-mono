package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.Page

class Page(
    val id: String? = null,
    val url: String,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Page,
        attributes = mapOf(
            "id" to id,
            "url" to url,
        ),
    )
}
