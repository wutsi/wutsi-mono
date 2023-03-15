package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.CameraLensDirection
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CameraTest {

    @Test
    fun toWidget() {
        val obj = Camera(
            uploadUrl = "http://1.1.1.1/upload",
            name = "Yo",
            lensDirection = CameraLensDirection.external,
            action = Action(
                type = ActionType.Prompt,
            ),
        )

        val widget = obj.toWidget()

        Assertions.assertEquals(WidgetType.Camera, widget.type)
        Assertions.assertEquals(obj.action, widget.action)

        Assertions.assertEquals(3, widget.attributes.size)
        Assertions.assertEquals(obj.name, widget.attributes["name"])
        Assertions.assertEquals(obj.uploadUrl, widget.attributes["uploadUrl"])
        Assertions.assertEquals(obj.lensDirection, widget.attributes["lensDirection"])

        Assertions.assertEquals(0, widget.children.size)
    }
}
