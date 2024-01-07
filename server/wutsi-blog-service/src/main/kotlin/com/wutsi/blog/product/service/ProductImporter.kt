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
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Date
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
        val path = service.downloadPath(store)
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
            service.downloadFile(record.get("file_link"), path, product)
        } catch (ex: Exception) {
            LOGGER.warn("Download error: " + record.get("file_link"), ex)
            errors.add(
                ImportError(row, ErrorCode.PRODUCT_FILE_LINK_UNABLE_TO_DOWNLOAD)
            )
        }

        try {
            service.downloadImage(record.get("image_link"), path, product)
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
}
