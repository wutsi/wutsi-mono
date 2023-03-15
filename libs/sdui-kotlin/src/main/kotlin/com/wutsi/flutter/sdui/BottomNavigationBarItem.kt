package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class BottomNavigationBarItem(
    val icon: String,
    val caption: String? = null,
    val action: Action,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.BottomNavigationBarItem,
        attributes = mapOf(
            "icon" to icon,
            "caption" to caption,
        ),
        action = action,
    )
}
