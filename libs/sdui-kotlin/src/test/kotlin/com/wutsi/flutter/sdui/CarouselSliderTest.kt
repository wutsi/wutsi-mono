package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class CarouselSliderTest {
    @Test
    fun toWidget() {
        val obj = CarouselSlider(
            children = listOf(Page(url = "xxx"), Container()),
            height = 1000.0,
            aspectRatio = 8.0 / 10,
            reverse = true,
            enableInfiniteScroll = false,
            viewportFraction = .9,
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.CarouselSlider, widget.type)
        assertNull(widget.action)

        assertEquals(obj.height, widget.attributes["height"])
        assertEquals(obj.aspectRatio, widget.attributes["aspectRatio"])
        assertEquals(obj.reverse, widget.attributes["reverse"])
        assertEquals(obj.enableInfiniteScroll, widget.attributes["enableInfiniteScroll"])
        assertEquals(obj.viewportFraction, widget.attributes["viewportFraction"])

        assertEquals(2, widget.children.size)
    }
}
