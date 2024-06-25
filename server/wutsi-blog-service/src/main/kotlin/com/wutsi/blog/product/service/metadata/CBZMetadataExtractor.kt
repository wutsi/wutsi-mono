package com.wutsi.blog.product.service.metadata

import com.wutsi.blog.product.dao.PageRepository
import com.wutsi.blog.product.domain.PageEntity
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.service.DocumentMetadataExtractor
import com.wutsi.platform.core.storage.MimeTypes
import com.wutsi.platform.core.storage.StorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Service
class CBZMetadataExtractor(
    private val dao: PageRepository,
    private val storage: StorageService,
) : DocumentMetadataExtractor {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CBZMetadataExtractor::class.java)
    }

    override fun extract(file: File, product: ProductEntity) {
        val zis = ZipInputStream(FileInputStream(file))
        zis.use {
            var numberOfPages = 0
            while (true) {
                val entry = zis.nextEntry ?: break
                if (!entry.isDirectory) {
                    if (toPage(zis, entry, numberOfPages + 1, product) != null) {
                        numberOfPages++
                    }
                }
            }
            product.numberOfPages = numberOfPages
            product.fileContentLength = file.length()
            product.fileContentType = CONTENT_TYPE
        }
    }

    private fun toPage(
        zis: ZipInputStream,
        entry: ZipEntry,
        number: Int,
        product: ProductEntity,
    ): PageEntity? {
        // Store page locally
        if (!MimeTypes.isImage(entry.name)) {
            LOGGER.info("$number - Ignoring ${entry.name} - Not an image")
            return null
        }

        val extension = MimeTypes.extension(entry.name)
        val file = File.createTempFile(entry.name, "." + extension)
        val fos = FileOutputStream(file)
        LOGGER.info("$number - Unzipping ${entry.name} to $file")
        val buffer = ByteArray(10 * 1024) // 10K
        fos.use {
            while (true) {
                val len = zis.read(buffer)
                if (len > 0) {
                    fos.write(buffer, 0, len)
                } else {
                    break
                }
            }
        }

        // Store remotely
        val fis = FileInputStream(file)
        fis.use {
            val contentUrl = storage.store("product/${product.id}/page/$number.$extension", fis, contentType).toString()
            LOGGER.info("  Storing $file to $contentUrl")

            val page = dao.findByProductAndNumber(product, number)
                ?: PageEntity(
                    product = product,
                    number = number
                )

            page.contentUrl = contentUrl
            dao.save(page)
            return page
        }
    }
}
