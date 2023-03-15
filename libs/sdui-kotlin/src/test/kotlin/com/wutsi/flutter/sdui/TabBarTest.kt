package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class TabBarTest {
    @Test
    fun toWidget() {
        val obj = TabBar(
            tabs = listOf(Container(), Container()),
        )

        val widget = obj.toWidget()
        assertEquals(WidgetType.TabBar, widget.type)
        assertEquals(0, widget.attributes.size)
        assertNull(widget.action)
        assertEquals(2, widget.children.size)
    }
}
