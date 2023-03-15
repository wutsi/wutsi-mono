package com.wutsi.platform.core.logging

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.slf4j.Logger
import java.io.IOException
import java.util.Optional
import kotlin.test.assertEquals

class DefaultKVLoggerTest {
    private lateinit var logger: Logger
    private lateinit var requestKv: DefaultKVLogger

    @BeforeEach
    fun setUp() {
        logger = mock(Logger::class.java)
        requestKv = DefaultKVLogger(logger, LoggerEncoder())
    }

    @Test
    fun shouldLog() {
        // Given
        requestKv.add("foo", "bar")
        requestKv.add("john", "doe")
        requestKv.add("valueLong", 1L)
        requestKv.add("valueInt", 2)
        requestKv.add("valueDouble", 3.5)
        requestKv.add("valueOpt", Optional.of(1))
        requestKv.add("valueCollection", listOf(1, 2))

        // When
        requestKv.log()

        // Then
        verify(logger).info("foo=bar john=doe valueCollection=\"1 2\" valueDouble=3.5 valueInt=2 valueLong=1 valueOpt=1")
    }

    @Test
    fun shouldNotLogWhenEmpty() {
        // When
        requestKv.log()

        // Then
        verify(logger, never()).info(anyString())
    }

    @Test
    fun shouldLogMultiValue() {
        // Given
        requestKv.add("foo", "john")
        requestKv.add("foo", "doe")

        // When
        requestKv.log()

        // Then
        verify(logger).info("foo=\"john doe\"")
    }

    @Test
    fun shouldLogWithSortedKeys() {
        // Given
        requestKv.add("Z", "bar")
        requestKv.add("A", "doe")

        // When
        requestKv.log()

        // Then
        verify(logger).info("A=doe Z=bar")
    }

    @Test
    fun shouldLogException() {
        // Given
        val ex = IOException("error")
        requestKv.setException(ex)

        // When
        requestKv.log()

        // Then
        val msg = ArgumentCaptor.forClass(String::class.java)
        val exception = ArgumentCaptor.forClass(Throwable::class.java)
        verify(logger).error(msg.capture(), exception.capture())
        assertEquals("exception=java.io.IOException exception_message=error", msg.value)
        assertEquals(ex, exception.value)
    }

    @Test
    @Throws(Exception::class)
    fun shouldLogAMaximumOf10000Characters() {
        // Given
        val ch100 =
            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
        val longString = StringBuilder()
        for (i in 0..999) {
            longString.append(ch100).append('\n')
        }

        requestKv.add("foo", "bar")
        requestKv.add("john", "smith")
        requestKv.add("name", longString.toString())

        // When
        requestKv.log()

        // Then
        val msg = ArgumentCaptor.forClass(String::class.java)
        verify(logger).info(msg.capture())
        assertEquals(DefaultKVLogger.MAX_LENGTH, msg.value.length)
    }

    @Test
    fun shouldNotLogNullValue() {
        // Given
        requestKv.add("foo", "bar")
        requestKv.add("john", null as String?)

        // When
        requestKv.log()

        // Then
        verify(logger).info("foo=bar")
    }
}
