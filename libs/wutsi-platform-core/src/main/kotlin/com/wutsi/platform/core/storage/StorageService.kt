package com.wutsi.platform.core.storage

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

interface StorageService {
    fun contains(url: URL): Boolean

    @Throws(IOException::class)
    fun store(
        path: String,
        content: InputStream,
        contentType: String? = null,
        ttlSeconds: Int? = null,
        contentEncoding: String? = null,
        contentLength: Long? = null,
    ): URL

    @Throws(IOException::class)
    fun get(url: URL, os: OutputStream)

    fun toURL(path: String): URL

    fun visit(path: String, visitor: StorageVisitor)

    fun exists(url: URL): Boolean
}
