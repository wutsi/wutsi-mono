package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

data class DropdownMenuItem(
    val caption: String,
    val value: String,
    val icon: String? = null,
    val enabled: Boolean = true,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.DropdownMenuItem,
        attributes = mapOf(
            "caption" to caption,
            "icon" to icon,
            "enabled" to enabled,
            "value" to value,
        ),
    )
}
