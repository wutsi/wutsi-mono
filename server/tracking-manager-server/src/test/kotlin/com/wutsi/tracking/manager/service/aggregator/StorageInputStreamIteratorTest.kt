package com.wutsi.tracking.manager.service.aggregator

import com.nhaarman.mockitokotlin2.mock
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URL
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class StorageInputStreamIteratorTest {
    private lateinit var storage: StorageService
    private lateinit var iterator: StorageInputStreamIterator

    @BeforeEach
    fun setUp() {
        storage = mock()

        val urls = mutableListOf<URL>()
        urls.add(URL("http://www.google.ca/a.txt"))
        iterator = StorageInputStreamIterator(urls, storage)
    }

    @Test
    operator fun next() {
        assertNotNull(iterator.next())
        assertThrows<NoSuchElementException> { iterator.next() }
    }

    @Test
    @Throws(Exception::class)
    fun hasNext() {
        assertTrue(iterator.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun hasNextFalse() {
        iterator.next()
        assertFalse(iterator.hasNext())
    }
}
