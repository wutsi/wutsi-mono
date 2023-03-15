package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.BadgePosition
import com.wutsi.flutter.sdui.enums.BadgeShape
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class BadgeTest {

    @Test
    fun toWidget() {
        val obj = Badge(
            id = "111",
            child = Container(),
            shape = BadgeShape.Circle,
            color = "fff",
            borderRadius = 3.0,
            position = BadgePosition.BottomEnd,
            elevation = 1.0,
            backgroundColor = "eee",
            caption = "foo",
            fontSize = 55.0,
            padding = 11.0,
        )

        val widget = obj.toWidget()

        assertNull(widget.action)

        assertEquals(WidgetType.Badge, widget.type)

        assertEquals(10, widget.attributes.size)
        assertEquals(obj.id, widget.attributes["id"])
        assertEquals(obj.shape, widget.attributes["shape"])
        assertEquals(obj.color, widget.attributes["color"])
        assertEquals(obj.borderRadius, widget.attributes["borderRadius"])
        assertEquals(obj.position, widget.attributes["position"])
        assertEquals(obj.elevation, widget.attributes["elevation"])
        assertEquals(obj.backgroundColor, widget.attributes["backgroundColor"])
        assertEquals(obj.caption, widget.attributes["caption"])
        assertEquals(obj.fontSize, widget.attributes["fontSize"])
        assertEquals(obj.padding, widget.attributes["padding"])

        assertEquals(1, widget.children.size)
    }
}
