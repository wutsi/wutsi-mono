package com.wutsi.flutter.sdui

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class TabTest {
    @Test
    fun toWidget() {
        val obj = Tab(
            icon = "xx",
            caption = "yyy",
        )

        val widget = obj.toWidget()
        assertEquals(2, widget.attributes.size)
        assertEquals(obj.icon, widget.attributes["icon"])
        assertEquals(obj.caption, widget.attributes["caption"])
        assertNull(widget.action)
        assertEquals(0, widget.children.size)
    }
}
