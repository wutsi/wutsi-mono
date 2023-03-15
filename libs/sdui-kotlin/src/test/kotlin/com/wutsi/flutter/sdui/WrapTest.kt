package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.Axis
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class WrapTest {

    @Test
    fun toWidget() {
        val obj = Wrap(
            children = listOf(Container(), Container()),
            runSpacing = 1.0,
            spacing = 3.0,
            direction = Axis.Horizontal,
        )

        val widget = obj.toWidget()

        Assertions.assertEquals(WidgetType.Wrap, widget.type)
        Assertions.assertNull(widget.action)

        Assertions.assertEquals(3, widget.attributes.size)
        Assertions.assertEquals(obj.runSpacing, widget.attributes["runSpacing"])
        Assertions.assertEquals(obj.spacing, widget.attributes["spacing"])
        Assertions.assertEquals(obj.direction, widget.attributes["direction"])

        Assertions.assertEquals(2, widget.children.size)
    }
}
