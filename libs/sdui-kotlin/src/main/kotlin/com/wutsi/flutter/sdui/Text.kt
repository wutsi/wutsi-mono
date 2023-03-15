package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.flutter.sdui.enums.TextDecoration
import com.wutsi.flutter.sdui.enums.TextOverflow
import com.wutsi.flutter.sdui.enums.WidgetType

class Text(
    val caption: String,
    val color: String? = null,
    val bold: Boolean? = null,
    val italic: Boolean? = null,
    val size: Double? = null,
    val alignment: TextAlignment? = null,
    val overflow: TextOverflow? = null,
    val decoration: TextDecoration? = null,
    val maxLines: Int? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Text,
        attributes = mapOf(
            "id" to id,
            "caption" to caption,
            "color" to color,
            "bold" to bold,
            "italic" to italic,
            "size" to size,
            "alignment" to alignment,
            "overflow" to overflow,
            "decoration" to decoration,
            "maxLines" to maxLines,
        ),
    )
}
