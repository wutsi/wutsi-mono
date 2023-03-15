package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class GridView(
    val crossAxisCount: Int,
    val primary: Boolean? = null,
    val crossAxisSpacing: Double? = null,
    val mainAxisSpacing: Double? = null,
    val padding: Double? = null,
    val children: List<WidgetAware> = emptyList(),
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.GridView,
        children = children.map { it.toWidget() },
        attributes = mapOf(
            "crossAxisCount" to crossAxisCount,
            "primary" to primary,
            "crossAxisSpacing" to crossAxisSpacing,
            "mainAxisSpacing" to mainAxisSpacing,
            "padding" to padding,
        ),
    )
}
