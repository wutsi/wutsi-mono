package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

data class SearchableDropdown(
    val name: String,
    val value: String? = null,
    val hint: String? = null,
    val required: Boolean? = null,
    val children: List<DropdownMenuItem> = emptyList(),
    val url: String? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.SearchableDropdown,
        attributes = mapOf(
            "id" to id,
            "name" to name,
            "value" to value,
            "hint" to hint,
            "required" to required,
            "url" to url,
        ),
        children = children.map { it.toWidget() },
    )
}
