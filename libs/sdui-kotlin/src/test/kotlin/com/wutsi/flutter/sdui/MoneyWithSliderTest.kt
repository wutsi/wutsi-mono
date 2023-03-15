package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class MoneyWithSliderTest {
    @Test
    fun toWidget() {
        val input = MoneyWithSlider(
            moneyColor = "1111",
            name = "foo",
            maxLength = 7,
            maxValue = 50000,
            currency = "XAF",
            value = 10000,
            sliderColor = "2222",
            numberFormat = "xxx",
            id = "222",
        )

        val widget = input.toWidget()

        assertEquals(WidgetType.MoneyWithSlider, widget.type)
        assertNull(widget.action)

        assertEquals(9, widget.attributes.size)
        Assertions.assertEquals(input.id, widget.attributes["id"])
        assertEquals(input.value, widget.attributes["value"])
        assertEquals(input.moneyColor, widget.attributes["moneyColor"])
        assertEquals(input.sliderColor, widget.attributes["sliderColor"])
        assertEquals(input.name, widget.attributes["name"])
        assertEquals(input.maxLength, widget.attributes["maxLength"])
        assertEquals(input.currency, widget.attributes["currency"])
        assertEquals(input.maxValue, widget.attributes["maxValue"])
        assertEquals(input.numberFormat, widget.attributes["numberFormat"])

        assertEquals(0, widget.children.size)
    }
}
