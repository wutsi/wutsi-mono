package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

data class PinWithKeyboard(
    val name: String,
    val color: String? = null,
    val hideText: Boolean? = null,
    val maxLength: Int? = null,
    val pinSize: Double? = null,
    val keyboardButtonSize: Double? = null,
    val action: Action? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.PinWithKeyboard,
        attributes = mapOf(
            "id" to id,
            "name" to name,
            "color" to color,
            "maxLength" to maxLength,
            "hideText" to hideText,
            "pinSize" to pinSize,
            "keyboardButtonSize" to keyboardButtonSize,
        ),
        action = action,
    )
}
