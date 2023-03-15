package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.Axis
import com.wutsi.flutter.sdui.enums.WidgetType

data class ListView(
    val direction: Axis? = null,
    val separator: Boolean? = null,
    val separatorColor: String? = null,
    val children: List<WidgetAware>,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.ListView,
        attributes = mapOf(
            "id" to id,
            "direction" to direction,
            "separator" to separator,
            "separatorColor" to separatorColor,
        ),
        children = children.map { it.toWidget() },
    )
}
