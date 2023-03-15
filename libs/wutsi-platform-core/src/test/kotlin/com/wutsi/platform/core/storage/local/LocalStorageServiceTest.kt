package com.wutsi.platform.core.storage.local

import com.wutsi.platform.core.storage.StorageVisitor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL

internal class LocalStorageServiceTest {
    private val directory = System.getProperty("user.home") + "/tmp/wutsi"
    private val baseUrl = "http://localhost:999/storage"
    private val storage = LocalStorageService(directory, baseUrl)

    @BeforeEach
    fun setUp() {
        delete(File(directory))
    }

    @Test
    fun contains() {
        assertTrue(storage.contains(URL("$baseUrl/1/2/text.txt")))
        assertFalse(storage.contains(URL("https://www.google.com/1/2/text.txt")))
    }

    @Test
    fun store() {
        val content = ByteArrayInputStream("hello".toByteArray())
        val result = storage.store("document/test.txt", content, "text/plain")

        assertNotNull(result)
        assertEquals(URL("http://localhost:999/storage/document/test.txt"), result)
    }

    @Test
    fun get() {
        val content = ByteArrayInputStream("hello world".toByteArray())
        val result = storage.store("document/test.txt2", content, "text/plain")

        val os = ByteArrayOutputStream()
        storage.get(result, os)

        assertEquals(os.toString(), "hello world")
    }

    @Test
    fun visitor() {
        val content = ByteArrayInputStream("hello".toByteArray())
        storage.store("file.txt", content, "text/plain")
        storage.store("a/file-a1.txt", content, "text/plain")
        storage.store("a/file-a2.txt", content, "text/plain")
        storage.store("a/b/file-ab1.txt", content, "text/plain")
        storage.store("a/b/c/file-abc1.txt", content, "text/plain")

        val urls = mutableListOf<URL>()
        val visitor = object : StorageVisitor {
            override fun visit(url: URL) {
                urls.add(url)
            }
        }
        storage.visit("a", visitor)

        assertEquals(4, urls.size)
        assertTrue(urls.contains(URL("$baseUrl/a/file-a1.txt")))
        assertTrue(urls.contains(URL("$baseUrl/a/file-a2.txt")))
        assertTrue(urls.contains(URL("$baseUrl/a/b/file-ab1.txt")))
        assertTrue(urls.contains(URL("$baseUrl/a/b/c/file-abc1.txt")))
    }

    private fun delete(file: File) {
        if (file.isDirectory) {
            val children = file.listFiles()
            if (children != null) {
                for (child in children) {
                    delete(child)
                }
            }
        }
        file.delete()
    }
}
