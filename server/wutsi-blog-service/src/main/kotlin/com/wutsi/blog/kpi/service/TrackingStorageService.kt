package com.wutsi.blog.kpi.service

import com.wutsi.platform.core.storage.StorageService
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

class TrackingStorageService(
    private val delegate: StorageService,
) {
    fun get(url: URL, output: OutputStream) {
        delegate.get(url, output)
    }

    fun store(path: String, input: InputStream, contentType: String?) {
        delegate.store(path, input, contentType)
    }
}
