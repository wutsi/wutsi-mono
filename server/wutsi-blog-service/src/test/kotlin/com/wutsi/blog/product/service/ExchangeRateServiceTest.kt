package com.wutsi.blog.product.service

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test


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
        assertEquals(1.0, service.convert(600, 1.0 / 656.0))
        assertEquals(2.0, service.convert(1000, 1.0 / 656.0))
        assertEquals(4.0, service.convert(2000, 1.0 / 616.0))
        assertEquals(9.0, service.convert(5000, 1.0 / 616.0))
    }

    @Test
    fun convertToInternationalPrice() {
        assertEquals(2.0, service.convertToInternationalPrice(600, 1.0 / 656.0))
        assertEquals(3.0, service.convertToInternationalPrice(1000, 1.0 / 656.0))
        assertEquals(5.0, service.convertToInternationalPrice(2000, 1.0 / 616.0))
        assertEquals(10.0, service.convertToInternationalPrice(5000, 1.0 / 616.0))
    }
}
