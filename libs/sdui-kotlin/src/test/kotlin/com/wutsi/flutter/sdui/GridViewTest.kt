package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class GridViewTest {
    @Test
    fun toWidget() {
        val obj = GridView(
            children = listOf(Container(), Container()),
            padding = 10.0,
            primary = true,
            crossAxisCount = 2,
            crossAxisSpacing = 10.0,
            mainAxisSpacing = 11.0,
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.GridView, widget.type)
        assertNull(widget.action)

        assertEquals(5, widget.attributes.size)
        assertEquals(obj.padding, widget.attributes["padding"])
        assertEquals(obj.primary, widget.attributes["primary"])
        assertEquals(obj.crossAxisCount, widget.attributes["crossAxisCount"])
        assertEquals(obj.crossAxisSpacing, widget.attributes["crossAxisSpacing"])
        assertEquals(obj.mainAxisSpacing, widget.attributes["mainAxisSpacing"])

        assertEquals(2, widget.children.size)
    }
}
