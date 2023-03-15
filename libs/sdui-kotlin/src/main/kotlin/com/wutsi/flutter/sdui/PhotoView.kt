package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class PhotoView(
    val url: String,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.PhotoView,
        attributes = mapOf(
            "url" to url,
        ),
    )
}
