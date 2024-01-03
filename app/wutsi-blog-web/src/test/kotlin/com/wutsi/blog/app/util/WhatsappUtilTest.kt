package com.wutsi.blog.app.util

import kotlin.test.Test
import kotlin.test.assertEquals

class WhatsappUtilTest {
    @Test
    fun url() {
        assertEquals("https://wa.me/237999999999", WhatsappUtil.url("237999999999"))
        assertEquals("https://wa.me/237999999999?text=Yo+man", WhatsappUtil.url("237999999999", "Yo man"))
        assertEquals(
            "https://wa.me/237999999999?text=Yo+man%0Ahttp%3A%2F%2Fw.com%2F1",
            WhatsappUtil.url("237999999999", "Yo man", "http://w.com/1")
        )
    }
}
