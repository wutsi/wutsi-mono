package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

data class ListItemSwitch(
    val name: String,
    val selected: Boolean = false,
    val caption: String,
    val subCaption: String? = null,
    val icon: String? = null,
    val action: Action? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.ListItemSwitch,
        attributes = mapOf(
            "id" to id,
            "caption" to caption,
            "subCaption" to subCaption,
            "icon" to icon,
            "name" to name,
            "selected" to selected,
        ),
        action = action,
    )
}
