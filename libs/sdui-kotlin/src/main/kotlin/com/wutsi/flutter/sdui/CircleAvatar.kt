package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.CircleAvatar

class CircleAvatar(
    val radius: Double? = null,
    val backgroundColor: String? = null,
    val foregroundColor: String? = null,
    val child: WidgetAware,
    val action: Action? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = CircleAvatar,
        attributes = mapOf(
            "id" to id,
            "radius" to radius,
            "backgroundColor" to backgroundColor,
            "foregroundColor" to foregroundColor,
        ),
        children = listOf(child.toWidget()),
        action = action,
    )
}
