package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class DropdownMenuItemTest {
    @Test
    fun toWidget() {
        val item = DropdownMenuItem(
            caption = "Yo",
            value = "man",
            icon = "000",
            enabled = true,
        )

        val widget = item.toWidget()

        assertEquals(WidgetType.DropdownMenuItem, widget.type)
        assertNull(widget.action)

        assertEquals(4, widget.attributes.size)
        assertEquals(item.caption, widget.attributes["caption"])
        assertEquals(item.value, widget.attributes["value"])
        assertEquals(item.icon, widget.attributes["icon"])
        assertEquals(item.enabled, widget.attributes["enabled"])

        assertEquals(0, widget.children.size)
    }
}
