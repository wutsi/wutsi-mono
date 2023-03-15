package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.CameraLensDirection
import com.wutsi.flutter.sdui.enums.WidgetType

class Camera(
    val name: String,
    val uploadUrl: String,
    val action: Action? = null,
    val lensDirection: CameraLensDirection? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Camera,
        action = action,
        attributes = mapOf(
            "name" to name,
            "uploadUrl" to uploadUrl,
            "lensDirection" to lensDirection,
        ),
    )
}
