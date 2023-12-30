package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.ProductImportEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.ImportError
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.blog.product.dto.ImportResult
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.jvm.optionals.getOrElse

@Service
class ProductImporter(
    private val service: ProductService,
    private val storage: StorageService,
    private val logger: KVLogger,
    private val storeService: StoreService,
    private val validator: ProductImporterValidator,
    private val categoryService: CategoryService,
    private val productImportService: ProductImportService,
    private val metadataExtractorProvider: DocumentMetadataExtractorProvider
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ProductImporter::class.java)
    }

    fun import(command: ImportProductCommand) {
        logger.add("command_store_id", command.storeId)
        logger.add("command_timestamp", command.timestamp)

        val store = storeService.findById(command.storeId)
        val productImport = execute(store, command.url)
        productImportService.notify(productImport)
    }

    private fun execute(store: StoreEntity, url: String): ProductImportEntity {
        val errors = mutableListOf<ImportError>()
        val path = "product/import/" +
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) +
            "/store/${store.id}" +
            "/" + UUID.randomUUID()
        logger.add("import_path", path)

        val result = try {
            val file = download(url)
            val tmp = import(store, url, file, path, errors)
            val products = service.unpublishOthers(store, tmp.externalIds)
            tmp.copy(unpublishedCount = products.size)
        } catch (ex: Exception) {
            LOGGER.error("Import error", ex)
            errors.add(ImportError(0, ErrorCode.PRODUCT_IMPORT_FAILED))
            ImportResult(
                url = url,
                errors = errors
            )
        }

        logger.add("error_count", errors.size)

        // Store Errors
        storeService.onProductsImported(store)
        return productImportService.onImported(path, store, result)
    }

    private fun download(url: String): File {
        val file = File.createTempFile("import", ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            storage.get(URL(url), fout)
        }
        return file
    }

    private fun import(
        store: StoreEntity,
        url: String,
        file: File,
        path: String,
        errors: MutableList<ImportError>
    ): ImportResult {
        val externalIds = mutableListOf<String>()
        var imported = 0
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .setHeader(
                    "id",
                    "category_id",
                    "title",
                    "price",
                    "availability",
                    "description",
                    "image_link",
                    "file_link"
                )
                .setSkipHeaderRecord(true)
                .build(),
        )
        parser.use {
            var row = 1
            for (record in parser) {
                // Collect the ID
                externalIds.add(record.get("id").trim())

                // Import
                val product = import(row, store, record, path, errors)
                if (product != null) {
                    imported++
                }
                row++
            }
        }
        return ImportResult(
            url = url,
            externalIds = externalIds,
            errors = errors,
            importedCount = imported
        )
    }

    private fun import(
        row: Int,
        store: StoreEntity,
        record: CSVRecord,
        path: String,
        errors: MutableList<ImportError>
    ): ProductEntity? {
        // Validate
        val validation = validator.validate(row, record)
        if (validation.isNotEmpty()) {
            errors.addAll(validation)
            return null
        }

        val externalId = record.get("id")
        val product = service.findByExternalIdAndStore(externalId, store)
            .getOrElse {
                ProductEntity(
                    store = store,
                    externalId = externalId,
                    type = ProductType.EBOOK,
                )
            }

        product.price = record.get("price").toLong()
        product.available = (record.get("availability")?.trim()?.lowercase() == "in stock")
        product.description = record.get("description")?.trim()
        product.title = record.get("title").trim()

        try {
            product.category = categoryService.findById(record.get("category_id").toLong())
        } catch (ex: Exception) {
            LOGGER.warn("Bad category: " + record.get("category_id"), ex)
            errors.add(
                ImportError(row, ErrorCode.PRODUCT_CATEGORY_INVALID)
            )
        }

        try {
            downloadFile(record.get("file_link"), path, product)
        } catch (ex: Exception) {
            LOGGER.warn("Download error: " + record.get("file_link"), ex)
            errors.add(
                ImportError(row, ErrorCode.PRODUCT_FILE_LINK_UNABLE_TO_DOWNLOAD)
            )
        }

        try {
            downloadImage(record.get("image_link"), path, product)
        } catch (ex: Exception) {
            LOGGER.warn("Download error: " + record.get("image_link"), ex)
            errors.add(
                ImportError(row, ErrorCode.PRODUCT_IMAGE_LINK_UNABLE_TO_DOWNLOAD)
            )
        }

        return if (errors.isEmpty()) {
            product.status = ProductStatus.PUBLISHED
            if (product.publishedDateTime == null) {
                product.publishedDateTime = Date()
            }
            return service.save(product)
        } else {
            null
        }
    }

    fun downloadImage(link: String, path: String, product: ProductEntity) {
        val url = URL(link)
        if (storage.contains(url)) {
            product.imageUrl = link
        }

        // Download
        val img = ImageIO.read(url)
        val file = File.createTempFile(UUID.randomUUID().toString(), ".png")
        val out = FileOutputStream(file)
        try {
            ImageIO.write(img, "png", out)

            // Store
            val input = FileInputStream(file)
            input.use {
                product.imageUrl = storage.store("$path/${file.name}", input).toString()
            }
        } finally {
            out.close()
        }
    }

    private fun downloadFile(link: String, path: String, product: ProductEntity) {
        val url = URL(link)
        val ext = if (link.lastIndexOf(".") > 0) {
            link.substring(link.lastIndexOf("."))
        } else {
            ""
        }

        // Store locally
        val file = File.createTempFile(UUID.randomUUID().toString(), ext)
        val fout = FileOutputStream(file)
        fout.use {
            val cnn = url.openConnection() as HttpURLConnection
            try {
                IOUtils.copy(cnn.inputStream, fout)
                product.fileContentType = extractContentType(cnn.contentType)
                product.fileContentLength = cnn.contentLength.toLong()

                product.fileContentType?.let { contentType ->
                    val meta = metadataExtractorProvider.get(contentType)
                    meta?.extract(file, product)
                }

            } finally {
                cnn.disconnect()
            }
        }

        // Store to the cloud
        val input = FileInputStream(file)
        input.use {
            product.fileUrl = storage.store(
                "$path/${file.name}",
                input,
                contentType = product.fileContentType,
                contentLength = product.fileContentLength
            ).toString()
        }
    }

    private fun extractContentType(contentType: String?): String? {
        val i = contentType?.indexOf(';') ?: return null
        return if (i > 0) {
            contentType.substring(0, i).trim()
        } else {
            contentType
        }
    }
}
