package com.wutsi.platform.payment.provider.mtn.spring

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.payment.provider.mtn.model.MTNTokenResponse
import com.wutsi.platform.payment.provider.mtn.product.Product
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.health.Status

internal class MTNProductHealthIndicatorTest {
    private lateinit var product: Product
    private lateinit var hc: HealthIndicator

    @BeforeEach
    fun setUp() {
        product = mock()
        hc = MTNProductHealthIndicator("sandbox", product)
    }

    @Test
    fun up() {
        doReturn(MTNTokenResponse("")).whenever(product).token(any())
        val result = hc.health()

        assertEquals(Status.UP, result.status)
    }

    @Test
    fun down() {
        doThrow(RuntimeException::class).whenever(product).token(any())
        val result = hc.health()

        assertEquals(Status.DOWN, result.status)
    }
}
