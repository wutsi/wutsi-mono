package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class SpacerTest {
    @Test
    fun toWidget() {
        val spacer = Spacer(flex = 1)

        val widget = spacer.toWidget()

        assertNull(widget.action)

        assertEquals(WidgetType.Spacer, widget.type)

        assertEquals(1, widget.attributes.size)
        assertEquals(spacer.flex, widget.attributes["flex"])

        assertEquals(0, widget.children.size)
    }
}
