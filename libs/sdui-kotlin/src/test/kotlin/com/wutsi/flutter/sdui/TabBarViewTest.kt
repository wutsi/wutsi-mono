package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class TabBarViewTest {
    @Test
    fun toWidget() {
        val obj = TabBarView(
            children = listOf(Container(), Container()),
        )

        val widget = obj.toWidget()
        assertEquals(WidgetType.TabBarView, widget.type)
        assertEquals(0, widget.attributes.size)
        assertNull(widget.action)
        assertEquals(2, widget.children.size)
    }
}
