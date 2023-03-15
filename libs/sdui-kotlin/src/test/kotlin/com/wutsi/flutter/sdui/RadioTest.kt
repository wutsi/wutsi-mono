package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class RadioTest {

    @Test
    fun toWidget() {
        val radio = Radio(
            caption = "foo",
            subCaption = "bar",
            value = "1",
            groupValue = "2",
            id = "1111",
        )

        val widget = radio.toWidget()

        assertEquals(WidgetType.Radio, widget.type)
        assertNull(widget.action)

        assertEquals(5, widget.attributes.size)
        assertEquals(radio.id, widget.attributes["id"])
        assertEquals(radio.caption, widget.attributes["caption"])
        assertEquals(radio.subCaption, widget.attributes["subCaption"])
        assertEquals(radio.value, widget.attributes["value"])
        assertEquals(radio.groupValue, widget.attributes["groupValue"])

        assertEquals(0, widget.children.size)
    }
}
