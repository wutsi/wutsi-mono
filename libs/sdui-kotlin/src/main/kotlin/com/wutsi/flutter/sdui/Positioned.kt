package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class Positioned(
    val top: Double? = null,
    val bottom: Double? = null,
    val left: Double? = null,
    val right: Double? = null,
    val width: Double? = null,
    val height: Double? = null,
    val child: WidgetAware,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Positioned,
        attributes = mapOf(
            "top" to top,
            "bottom" to bottom,
            "left" to left,
            "right" to right,
            "width" to width,
            "height" to height,
        ),
        children = listOf(child.toWidget()),
    )
}
