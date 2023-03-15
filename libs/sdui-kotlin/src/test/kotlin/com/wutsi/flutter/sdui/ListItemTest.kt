package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class ListItemTest {

    @Test
    fun toWidget() {
        val item = ListItem(
            id = "111",
            caption = "foo",
            subCaption = "bar",
            iconLeft = "1",
            iconRight = "2",
            padding = 10.0,
            action = Action(),
            leading = Container(),
            trailing = Container(),
        )

        val widget = item.toWidget()

        assertEquals(WidgetType.ListItem, widget.type)
        assertNotNull(widget.action)

        assertEquals(8, widget.attributes.size)
        assertEquals(item.id, widget.attributes["id"])
        assertEquals(item.caption, widget.attributes["caption"])
        assertEquals(item.subCaption, widget.attributes["subCaption"])
        assertEquals(item.iconLeft, widget.attributes["iconLeft"])
        assertEquals(item.iconRight, widget.attributes["iconRight"])
        assertEquals(item.padding, widget.attributes["padding"])
        assertNotNull(widget.attributes["trailing"])
        assertNotNull(widget.attributes["leading"])

        assertEquals(0, widget.children.size)
    }
}
