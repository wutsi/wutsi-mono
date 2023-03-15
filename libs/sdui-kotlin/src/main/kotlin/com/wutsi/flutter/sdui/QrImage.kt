package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.QrImage

class QrImage(
    val data: String,
    val version: Int? = null,
    val size: Double? = null,
    val padding: Double? = null,
    val embeddedImageUrl: String? = null,
    val embeddedImageSize: Double? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = QrImage,
        attributes = mapOf(
            "data" to data,
            "version" to version,
            "size" to size,
            "padding" to padding,
            "embeddedImageUrl" to embeddedImageUrl,
            "embeddedImageSize" to embeddedImageSize,
        ),
    )
}
