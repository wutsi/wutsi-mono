package com.wutsi.application.widget

import com.wutsi.application.util.StringUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import kotlin.math.max

class AvatarWidget(
    private val displayName: String,
    private val pictureUrl: String? = null,
    private val radius: Double,
    private val action: Action? = null,
) : CompositeWidgetAware() {
    override fun toWidgetAware(): WidgetAware =
        CircleAvatar(
            action = action,
            radius = radius,
            child = if (pictureUrl.isNullOrEmpty()) {
                Text(
                    StringUtil.initials(displayName),
                    bold = true,
                    size = max(14.0, radius - 8.0),
                )
            } else {
                Image(pictureUrl)
            },
        )
}
