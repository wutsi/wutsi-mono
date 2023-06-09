package com.wutsi.tracking.manager.service.aggregator

import com.wutsi.platform.core.storage.StorageService
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL

class StorageInputStreamIterator(
    val urls: List<URL>,
    val storage: StorageService,
) : InputStreamIterator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StorageInputStreamIterator::class.java)
    }

    private var index = 0

    override fun next(): InputStream {
        ByteArrayOutputStream().use { out ->
            try {
                val url = urls[index++]
                LOGGER.debug("Loading $url")

                storage.get(url, out)
                return ByteArrayInputStream(out.toByteArray())
            } catch (ex: IndexOutOfBoundsException) {
                throw NoSuchElementException()
            }
        }
    }

    override fun hasNext(): Boolean =
        index < urls.size
}
