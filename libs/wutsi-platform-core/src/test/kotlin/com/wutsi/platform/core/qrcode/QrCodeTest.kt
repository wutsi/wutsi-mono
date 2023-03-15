package com.wutsi.platform.core.qrcode

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Clock
import kotlin.test.assertEquals

internal class QrCodeTest : KeyProvider {
    private val keyId = "1"
    private val key = "123456"
    private val now = 10000L

    private lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        clock = mock()
        doReturn(now).whenever(clock).millis()
    }

    override fun getKeyId(): String =
        keyId

    override fun getKey(id: String): String =
        key

    @Test
    fun encode() {
        val qr = QrCode("account", "123")

        val data = qr.encode(this, clock)

        assertEquals("YWNjb3VudCwxMjMsMjE0NzQ4MzY0Nw==.MQ==.MmE5N2UxZWRhZjVhZTE5YmNmZDZkNjE4OWY1NWFiNzQ=", data)
    }

    @Test
    fun encodeWithTTL() {
        val qr = QrCode("account", "123", 10)

        val data = qr.encode(this, clock)

        assertEquals("YWNjb3VudCwxMjMsMjA=.MQ==.MzI0MDgxMDdlNDI4MDU0NDJhMDBmODM0MjNkOWRjOGI=", data)
    }

    @Test
    fun decode() {
        val qr =
            QrCode.decode("YWNjb3VudCwxMjMsMjE0NzQ4MzY0Nw==.MQ==.MmE5N2UxZWRhZjVhZTE5YmNmZDZkNjE4OWY1NWFiNzQ=", this)

        assertEquals("account", qr.type)
        assertEquals("123", qr.value)
    }

    @Test
    fun decodeUrl() {
        val qr = QrCode.decode("https://www.wutsi.com", this)

        assertEquals("URL", qr.type)
        assertEquals("https://www.wutsi.com", qr.value)
    }

    @Test
    fun decodeExpired() {
        doReturn(now - 10 * 1000).whenever(clock).millis()
        assertThrows<ExpiredQrCodeException> {
            QrCode.decode("YWNjb3VudCwxMjMsMjA=.MQ==.MzI0MDgxMDdlNDI4MDU0NDJhMDBmODM0MjNkOWRjOGI=", this)
        }
    }

    @Test
    fun decodeCorrupted() {
        assertThrows<CorruptedQrCodeException> {
            QrCode.decode("YWNjb3VudCwxMjMsMjE0NzQ4MzY0Nw==.MQ==.MzI0MDgxMDdlNDI4MDU0NDJhMDBmODM0MjNkOWRjOGI=", this)
        }
    }

    @Test
    fun decodeMalformed() {
        assertThrows<QrCodeException> {
            QrCode.decode("YWNjb3VudCwxMjMsMjA=.MQ==.MzI0MDgxMDdlNDI4MDU0NDJhMDBmODM0MjNkOWRjOGI=.99999", this)
        }
    }
}
