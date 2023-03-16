package com.wutsi.platform.payment.provider.flutterwave.spring

import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.payment.provider.flutterwave.FWGateway
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.health.Status

internal class FlutterwaveHealthIndicatorTest {
    private lateinit var gateway: FWGateway
    private lateinit var hc: HealthIndicator

    @BeforeEach
    fun setUp() {
        gateway = mock()
        hc = FlutterwaveHealthIndicator(gateway)
    }

    @Test
    fun up() {
        val result = hc.health()
        assertEquals(Status.UP, result.status)
    }

    @Test
    fun down() {
        doThrow(RuntimeException::class).whenever(gateway).health()

        val result = hc.health()

        assertEquals(Status.DOWN, result.status)
    }
}
