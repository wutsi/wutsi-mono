package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class QrView(
    val submitUrl: String,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.QrView,
        attributes = mapOf(
            "submitUrl" to submitUrl,
        ),
    )
}
