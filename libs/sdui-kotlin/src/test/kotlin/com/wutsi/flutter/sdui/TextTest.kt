package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.TextAlignment.Center
import com.wutsi.flutter.sdui.enums.TextDecoration.Strikethrough
import com.wutsi.flutter.sdui.enums.TextOverflow.Clip
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class TextTest {
    @Test
    fun toWidget() {
        val text = Text(
            caption = "Yo",
            color = "#000000",
            bold = true,
            italic = true,
            size = 12.0,
            alignment = Center,
            overflow = Clip,
            decoration = Strikethrough,
            maxLines = 3,
            id = "111",
        )

        val widget = text.toWidget()

        assertEquals(WidgetType.Text, widget.type)

        assertNull(widget.action)

        assertEquals(10, widget.attributes.size)
        assertEquals(text.id, widget.attributes["id"])
        assertEquals(text.caption, widget.attributes["caption"])
        assertEquals(text.color, widget.attributes["color"])
        assertEquals(text.bold, widget.attributes["bold"])
        assertEquals(text.italic, widget.attributes["italic"])
        assertEquals(text.size, widget.attributes["size"])
        assertEquals(text.alignment, widget.attributes["alignment"])
        assertEquals(text.overflow, widget.attributes["overflow"])
        assertEquals(text.decoration, widget.attributes["decoration"])
        assertEquals(text.maxLines, widget.attributes["maxLines"])

        assertEquals(0, widget.children.size)
    }
}
