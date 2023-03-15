package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.Axis
import com.wutsi.flutter.sdui.enums.WidgetType

class Wrap(
    val children: List<WidgetAware> = emptyList(),
    val spacing: Double? = null,
    val runSpacing: Double? = null,
    val direction: Axis? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Wrap,
        attributes = mapOf(
            "spacing" to spacing,
            "runSpacing" to runSpacing,
            "direction" to direction,
        ),
        children = children.map { it.toWidget() },
    )
}
