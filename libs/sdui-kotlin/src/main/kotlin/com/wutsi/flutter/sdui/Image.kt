package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.BoxFit
import com.wutsi.flutter.sdui.enums.WidgetType.Image

class Image(
    val url: String,
    val width: Double? = null,
    val height: Double? = null,
    val fit: BoxFit? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Image,
        attributes = mapOf(
            "id" to id,
            "url" to url,
            "width" to width,
            "height" to height,
            "fit" to fit,
        ),
    )
}
