package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.ButtonType.Elevated
import com.wutsi.flutter.sdui.enums.WidgetType.Button

class Button(
    val id: String? = null,
    val caption: String = "",
    val type: ButtonType = Elevated,
    val padding: Double? = 15.0,
    val action: Action? = null,
    val stretched: Boolean? = null,
    val icon: String? = null,
    val iconSize: Double? = null,
    val iconColor: String? = null,
    val color: String? = null,
    val fontSize: Double? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Button,
        action = action,
        attributes = mapOf(
            "id" to id,
            "caption" to caption,
            "padding" to padding,
            "type" to type,
            "stretched" to stretched,
            "icon" to icon,
            "iconSize" to iconSize,
            "iconColor" to iconColor,
            "color" to color,
            "fontSize" to fontSize,
        ),
    )
}
