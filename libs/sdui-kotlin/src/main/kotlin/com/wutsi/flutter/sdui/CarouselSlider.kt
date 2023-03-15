package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class CarouselSlider(
    val aspectRatio: Double? = null,
    val height: Double? = null,
    val viewportFraction: Double? = null,
    val enableInfiniteScroll: Boolean? = null,
    val reverse: Boolean? = null,
    val children: List<WidgetAware> = emptyList(),
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.CarouselSlider,
        attributes = mapOf(
            "aspectRatio" to aspectRatio,
            "height" to height,
            "viewportFraction" to viewportFraction,
            "enableInfiniteScroll" to enableInfiniteScroll,
            "reverse" to reverse,
        ),
        children = children.map { it.toWidget() },
    )
}
