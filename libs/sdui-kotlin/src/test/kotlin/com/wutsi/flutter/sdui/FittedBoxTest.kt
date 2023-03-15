package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.BoxFit
import com.wutsi.flutter.sdui.enums.Clip
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class FittedBoxTest {
    @Test
    fun toWidget() {
        val obj = FittedBox(
            clip = Clip.antiAlias,
            fit = BoxFit.fitWidth,
            child = Container(),
            alignment = Alignment.Center,
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.FittedBox, widget.type)
        assertNull(widget.action)

        assertEquals(3, widget.attributes.size)
        assertEquals(obj.clip, widget.attributes["clip"])
        assertEquals(obj.fit, widget.attributes["fit"])
        assertEquals(obj.alignment, widget.attributes["alignment"])

        assertEquals(1, widget.children.size)
    }
}
