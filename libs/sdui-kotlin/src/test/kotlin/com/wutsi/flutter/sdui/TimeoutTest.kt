package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TimeoutTest {

    @Test
    fun toWidget() {
        val obj = Timeout(
            url = "http://www.google.ca",
            delay = 15,
        )

        val widget = obj.toWidget()

        Assertions.assertEquals(WidgetType.Timeout, widget.type)
        Assertions.assertNull(widget.action)

        Assertions.assertEquals(2, widget.attributes.size)
        Assertions.assertEquals(obj.url, widget.attributes["url"])
        Assertions.assertEquals(obj.delay, widget.attributes["delay"])

        Assertions.assertEquals(0, widget.children.size)
    }
}
