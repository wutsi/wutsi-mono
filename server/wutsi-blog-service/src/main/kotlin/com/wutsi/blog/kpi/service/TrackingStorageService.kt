package com.wutsi.blog.kpi.service

import com.wutsi.platform.core.storage.StorageService
import java.io.InputStream
import java.io.OutputStream

class TrackingStorageService(
    private val delegate: StorageService,
) {
    fun get(path: String, output: OutputStream) {
        val url = delegate.toURL(path)
        delegate.get(url, output)
    }

    fun store(path: String, input: InputStream, contentType: String?) {
        delegate.store(path, input, contentType)
    }
}
