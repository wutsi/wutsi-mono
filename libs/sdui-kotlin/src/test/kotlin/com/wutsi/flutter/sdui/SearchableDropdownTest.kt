package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class SearchableDropdownTest {

    @Test
    fun toWidget() {
        val item = SearchableDropdown(
            id = "111",
            name = "Yo",
            value = "man",
            required = true,
            hint = "foo bar",
            url = "https://www.google.ca",
            children = listOf(DropdownMenuItem("y", "x")),
        )

        val widget = item.toWidget()

        assertEquals(WidgetType.SearchableDropdown, widget.type)
        assertNull(widget.action)

        assertEquals(6, widget.attributes.size)
        assertEquals(item.id, widget.attributes["id"])
        assertEquals(item.name, widget.attributes["name"])
        assertEquals(item.value, widget.attributes["value"])
        assertEquals(item.required, widget.attributes["required"])
        assertEquals(item.hint, widget.attributes["hint"])
        assertEquals(item.url, widget.attributes["url"])

        assertEquals(1, widget.children.size)
    }
}
