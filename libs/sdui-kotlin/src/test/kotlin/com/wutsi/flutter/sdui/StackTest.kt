package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class StackTest {
    @Test
    fun toWidget() {
        val obj = Stack(
            children = listOf(Container(), Container()),
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.Stack, widget.type)
        assertNull(widget.action)

        assertEquals(0, widget.attributes.size)

        assertEquals(2, widget.children.size)
    }
}
