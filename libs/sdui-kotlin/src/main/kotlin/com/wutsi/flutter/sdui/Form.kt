package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.Form

class Form(
    val children: List<WidgetAware> = emptyList(),
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Form,
        children = children.map { it.toWidget() },
    )
}
