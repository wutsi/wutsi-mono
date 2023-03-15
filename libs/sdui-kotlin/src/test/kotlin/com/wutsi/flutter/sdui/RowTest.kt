package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.CrossAxisAlignment.baseline
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.center
import com.wutsi.flutter.sdui.enums.MainAxisSize.max
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class RowTest {
    @Test
    fun toWidget() {
        val obj = Row(
            children = listOf(Container(), Container()),
            mainAxisAlignment = center,
            crossAxisAlignment = baseline,
            mainAxisSize = max,
            id = "111",
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.Row, widget.type)
        assertNull(widget.action)

        assertEquals(4, widget.attributes.size)
        assertEquals(obj.id, widget.attributes["id"])
        assertEquals(obj.mainAxisAlignment, widget.attributes["mainAxisAlignment"])
        assertEquals(obj.crossAxisAlignment, widget.attributes["crossAxisAlignment"])
        assertEquals(obj.mainAxisSize, widget.attributes["mainAxisSize"])

        assertEquals(2, widget.children.size)
    }
}
