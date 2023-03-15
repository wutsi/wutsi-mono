package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class AspectRatio(
    val aspectRatio: Double = 1.0,
    val child: WidgetAware? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.AspectRatio,
        attributes = mapOf(
            "aspectRatio" to aspectRatio,
        ),
        children = if (child == null) emptyList() else listOf(child.toWidget()),
    )
}
