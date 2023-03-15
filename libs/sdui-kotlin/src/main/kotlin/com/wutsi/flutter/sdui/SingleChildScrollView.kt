package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.Axis
import com.wutsi.flutter.sdui.enums.WidgetType

class SingleChildScrollView(
    val scrollDirection: Axis? = null,
    val primary: Boolean? = null,
    val reverse: Boolean? = null,
    val padding: Double? = null,
    val child: WidgetAware? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.SingleChildScrollView,
        children = listOfNotNull(child).map { it.toWidget() },
        attributes = mapOf(
            "scrollDirection" to scrollDirection,
            "primary" to primary,
            "reverse" to reverse,
            "padding" to padding,
        ),
    )
}
