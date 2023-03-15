package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.DropdownButton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DropdownButtonTest {

    @Test
    fun toWidget() {
        val action = Action()
        val item = DropdownButton(
            id = "1111",
            name = "Yo",
            value = "man",
            required = true,
            hint = "foo bar",
            children = listOf(DropdownMenuItem("y", "x")),
            action = action,
            stretched = true,
            outlinedBorder = true,
        )

        val widget = item.toWidget()

        assertEquals(DropdownButton, widget.type)
        assertEquals(action, widget.action)

        assertEquals(7, widget.attributes.size)
        assertEquals(item.id, widget.attributes["id"])
        assertEquals(item.name, widget.attributes["name"])
        assertEquals(item.value, widget.attributes["value"])
        assertEquals(item.required, widget.attributes["required"])
        assertEquals(item.hint, widget.attributes["hint"])
        assertEquals(item.stretched, widget.attributes["stretched"])
        assertEquals(item.outlinedBorder, widget.attributes["outlinedBorder"])

        assertEquals(1, widget.children.size)
    }
}
