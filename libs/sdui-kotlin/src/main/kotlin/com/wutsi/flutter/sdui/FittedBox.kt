package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.BoxFit
import com.wutsi.flutter.sdui.enums.Clip
import com.wutsi.flutter.sdui.enums.WidgetType

class FittedBox(
    val fit: BoxFit? = null,
    val clip: Clip? = null,
    val alignment: Alignment? = null,
    val child: WidgetAware? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.FittedBox,
        attributes = mapOf(
            "fit" to fit,
            "clip" to clip,
            "alignment" to alignment,
        ),
        children = child?.let { listOf(child.toWidget()) } ?: emptyList(),
    )
}
