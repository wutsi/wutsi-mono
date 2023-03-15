package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

data class ListItem(
    val caption: String,
    val subCaption: String? = null,
    val iconLeft: String? = null,
    val iconRight: String? = null,
    val padding: Double? = null,
    val leading: WidgetAware? = null,
    val trailing: WidgetAware? = null,
    val action: Action? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.ListItem,
        attributes = mapOf(
            "id" to id,
            "caption" to caption,
            "subCaption" to subCaption,
            "iconLeft" to iconLeft,
            "iconRight" to iconRight,
            "padding" to padding,
            "trailing" to trailing?.toWidget(),
            "leading" to leading?.toWidget(),
        ),
        action = action,
    )
}
