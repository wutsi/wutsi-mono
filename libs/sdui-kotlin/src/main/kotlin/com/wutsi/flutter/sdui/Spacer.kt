package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.Spacer

class Spacer(
    val flex: Int = 1,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Spacer,
        attributes = mapOf(
            "flex" to flex,
        ),
    )
}
