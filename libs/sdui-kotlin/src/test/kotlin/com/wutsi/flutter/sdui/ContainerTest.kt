package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment.BottomCenter
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ContainerTest {
    @Test
    fun toWidget() {
        val container = Container(
            id = "111",
            padding = 1.0,
            alignment = BottomCenter,
            background = "aa0000",
            border = 2.0,
            borderColor = "cc0000",
            borderRadius = 3.0,
            margin = 4.0,
            child = Page(url = "xxx"),
            width = 111.0,
            height = 22.0,
            backgroundImageUrl = "https://www.img.com/1.png",
            action = Action(type = ActionType.Route),
        )

        val widget = container.toWidget()

        assertEquals(WidgetType.Container, widget.type)
        assertEquals(container.action, widget.action)

        assertEquals(11, widget.attributes.size)
        assertEquals(container.id, widget.attributes["id"])
        assertEquals(container.padding, widget.attributes["padding"])
        assertEquals(container.alignment, widget.attributes["alignment"])
        assertEquals(container.background, widget.attributes["background"])
        assertEquals(container.border, widget.attributes["border"])
        assertEquals(container.borderColor, widget.attributes["borderColor"])
        assertEquals(container.margin, widget.attributes["margin"])
        assertEquals(container.width, widget.attributes["width"])
        assertEquals(container.height, widget.attributes["height"])
        assertEquals(container.backgroundImageUrl, widget.attributes["backgroundImageUrl"])
        assertEquals(container.borderRadius, widget.attributes["borderRadius"])

        assertEquals(1, widget.children.size)
    }
}
