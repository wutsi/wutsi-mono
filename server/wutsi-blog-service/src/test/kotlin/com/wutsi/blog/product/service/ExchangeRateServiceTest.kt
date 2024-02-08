package com.wutsi.blog.product.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExchangeRateServiceTest {
    val service = ExchangeRateService()

    @Test
    fun getExchangeRate() {
        assertEquals(1.0 / 656.0, service.getExchangeRate("XAF", "EUR"))
        assertEquals(1.0 / 610.0, service.getExchangeRate("XAF", "USD"))
        assertEquals(1.0, service.getExchangeRate("XAF", "XOF"))

        assertEquals(1.0 / 610.0, service.getExchangeRate("XOF", "USD"))
    }

    @Test
    fun convert() {
        assertEquals(2.0, service.convert(1000, 1.0 / 656.0))
        assertEquals(3.0, service.convert(1500, 1.0 / 616.0))
    }
}