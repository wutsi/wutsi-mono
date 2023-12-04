package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.StoreEntity
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@Service
class ProductFeedDownloader {
    fun download(store: StoreEntity): File {
        val file = File.createTempFile("import", ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            URL(store.feedUrl).openStream().use { IOUtils.copy(it, fout) }
        }
        return file
    }
}
