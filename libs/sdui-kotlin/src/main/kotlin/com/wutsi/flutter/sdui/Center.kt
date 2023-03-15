package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class Center(
    val child: WidgetAware? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Center,
        children = if (child == null) emptyList() else listOf(child.toWidget()),
    )
}
