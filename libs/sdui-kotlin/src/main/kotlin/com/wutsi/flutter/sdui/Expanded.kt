package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class Expanded(
    val child: WidgetAware? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Expanded,
        children = listOfNotNull(child).map { it.toWidget() },
    )
}
