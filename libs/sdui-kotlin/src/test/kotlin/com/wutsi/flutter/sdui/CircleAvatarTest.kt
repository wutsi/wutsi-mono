package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CircleAvatarTest {

    @Test
    fun toWidget() {
        val obj = CircleAvatar(
            id = "111",
            radius = 30.0,
            backgroundColor = "xxx",
            foregroundColor = "vvv",
            child = Container(),
            action = Action(type = ActionType.Command),
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.CircleAvatar, widget.type)
        assertEquals(obj.action, widget.action)

        assertEquals(4, widget.attributes.size)
        assertEquals(obj.radius, widget.attributes["radius"])
        assertEquals(obj.id, widget.attributes["id"])
        assertEquals(obj.backgroundColor, widget.attributes["backgroundColor"])
        assertEquals(obj.foregroundColor, widget.attributes["foregroundColor"])

        assertEquals(1, widget.children.size)
    }
}
