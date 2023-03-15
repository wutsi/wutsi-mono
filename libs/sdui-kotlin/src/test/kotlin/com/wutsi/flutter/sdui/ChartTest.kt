package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class ChartTest {
    @Test
    fun toWidget() {
        val obj = Chart(
            title = "Title",
            series = listOf(
                listOf(
                    ChartData("Jan", 1.0),
                    ChartData("Feb", 2.0),
                    ChartData("Mar", 3.0),
                ),
                listOf(
                    ChartData("Jan", 10.0),
                    ChartData("Feb", 20.0),
                    ChartData("Mar", 30.0),
                ),
            ),
        )

        val widget = obj.toWidget()

        assertNull(widget.action)

        assertEquals(WidgetType.Chart, widget.type)

        assertEquals(2, widget.attributes.size)
        assertEquals(obj.title, widget.attributes["title"])
        assertEquals(obj.series, widget.attributes["series"])

        assertEquals(0, widget.children.size)
    }
}
