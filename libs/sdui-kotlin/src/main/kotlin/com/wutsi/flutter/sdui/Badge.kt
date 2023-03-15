package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.BadgePosition
import com.wutsi.flutter.sdui.enums.BadgeShape
import com.wutsi.flutter.sdui.enums.WidgetType

class Badge(
    val shape: BadgeShape? = null,
    val position: BadgePosition? = null,
    val color: String? = null,
    val backgroundColor: String? = null,
    val caption: String? = null,
    val borderRadius: Double? = null,
    val elevation: Double? = null,
    val padding: Double? = null,
    val fontSize: Double? = null,
    val child: WidgetAware? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Badge,
        children = if (child == null) emptyList() else listOf(child.toWidget()),
        attributes = mapOf(
            "id" to id,
            "shape" to shape,
            "position" to position,
            "color" to color,
            "backgroundColor" to backgroundColor,
            "caption" to caption,
            "borderRadius" to borderRadius,
            "elevation" to elevation,
            "fontSize" to fontSize,
            "padding" to padding,
        ),
    )
}
