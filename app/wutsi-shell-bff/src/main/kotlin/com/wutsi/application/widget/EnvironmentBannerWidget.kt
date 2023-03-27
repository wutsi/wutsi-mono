package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.TextAlignment

class EnvironmentBannerWidget(
    private val environment: String = "TEST",
    private val version: String? = null,
) : WidgetAware {
    override fun toWidget(): Widget =
        Container(
            alignment = Alignment.Center,
            border = 1.0,
            background = Theme.COLOR_WARNING_LIGHT,
            borderColor = Theme.COLOR_WARNING,
            width = Double.MAX_VALUE,
            child = Text(
                caption = "Environment: $environment" + (version?.let { " - v$it" } ?: ""),
                alignment = TextAlignment.Center,
                size = Theme.TEXT_SIZE_SMALL,
            ),
        ).toWidget()
}
