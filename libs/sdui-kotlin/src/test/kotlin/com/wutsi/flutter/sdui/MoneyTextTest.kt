package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class MoneyTextTest {
    @Test
    fun toWidget() {
        val obj = MoneyText(
            currency = "XAD",
            value = 100.0,
            color = "ff0000",
            numberFormat = "xxx",
            currencyFontSize = 8.0,
            valueFontSize = 20.0,
            bold = true,
            id = "1111",
            alignment = TextAlignment.Right,
            locale = "fr",
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.MoneyText, widget.type)
        assertNull(widget.action)

        assertEquals(10, widget.attributes.size)
        assertEquals(obj.id, widget.attributes["id"])
        assertEquals(obj.currency, widget.attributes["currency"])
        assertEquals(obj.value, widget.attributes["value"])
        assertEquals(obj.color, widget.attributes["color"])
        assertEquals(obj.numberFormat, widget.attributes["numberFormat"])
        assertEquals(obj.bold, widget.attributes["bold"])
        assertEquals(obj.currencyFontSize, widget.attributes["currencyFontSize"])
        assertEquals(obj.valueFontSize, widget.attributes["valueFontSize"])
        assertEquals(obj.alignment, widget.attributes["alignment"])
        assertEquals(obj.locale, widget.attributes["locale"])

        assertEquals(0, widget.children.size)
    }
}
