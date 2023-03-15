package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.WidgetType.Container

class Container(
    val alignment: Alignment? = null,
    val padding: Double? = null,
    val margin: Double? = null,
    val background: String? = null,
    val border: Double? = null,
    val borderRadius: Double? = null,
    val borderColor: String? = null,
    val width: Double? = null,
    val height: Double? = null,
    val backgroundImageUrl: String? = null,
    val child: WidgetAware? = null,
    val action: Action? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Container,
        attributes = mapOf(
            "id" to id,
            "alignment" to alignment,
            "padding" to padding,
            "margin" to margin,
            "background" to background,
            "border" to border,
            "borderRadius" to borderRadius,
            "borderColor" to borderColor,
            "height" to height,
            "width" to width,
            "backgroundImageUrl" to backgroundImageUrl,
        ),
        children = child?.let { listOf(it.toWidget()) } ?: emptyList(),
        action = action,
    )
}
