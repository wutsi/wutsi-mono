package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

data class DropdownButton(
    val name: String,
    val value: String? = null,
    val hint: String? = null,
    val required: Boolean? = null,
    val children: List<DropdownMenuItem> = emptyList(),
    val stretched: Boolean? = null,
    val outlinedBorder: Boolean? = null,
    val action: Action? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.DropdownButton,
        attributes = mapOf(
            "id" to id,
            "name" to name,
            "value" to value,
            "hint" to hint,
            "required" to required,
            "stretched" to stretched,
            "outlinedBorder" to outlinedBorder,
        ),
        children = children.map { it.toWidget() },
        action = action,
    )
}
