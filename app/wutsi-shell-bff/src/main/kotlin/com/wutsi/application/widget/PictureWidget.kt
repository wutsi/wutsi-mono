package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment

class PictureWidget(
    private val url: String,
    private val width: Double = 150.0,
    private val height: Double = 150.0,
    private val padding: Double? = null,
    private val border: Double? = null,
    private val action: Action? = null,
) : CompositeWidgetAware() {
    override fun toWidgetAware(): WidgetAware =
        Container(
            padding = padding,
            width = width,
            height = height,
            alignment = Alignment.Center,
            borderColor = border?.let { Theme.COLOR_PRIMARY_LIGHT },
            border = border,
            backgroundImageUrl = url,
            action = action,
        )
}
