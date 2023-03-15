package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.IconButton

class IconButton(
    val icon: String,
    val tooltip: String? = null,
    val size: Double? = null,
    val color: String? = null,
    val action: Action? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = IconButton,
        attributes = mapOf(
            "id" to id,
            "icon" to icon,
            "size" to size,
            "color" to color,
            "tooltip" to tooltip,
        ),
        action = action,
    )
}
