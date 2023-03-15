package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class Stack(
    val children: List<WidgetAware>,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Stack,
        children = children.map { it.toWidget() },
    )
}
