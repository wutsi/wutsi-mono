package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.ProductImportEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.ImportError
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.blog.product.dto.ImportResult
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
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
    private val eventStore: EventStore,
    private val productImportService: ProductImportService,
) {
    fun import(command: ImportProductCommand) {
        logger.add("command_store_id", command.storeId)
        logger.add("command_timestamp", command.timestamp)

        val store = storeService.findById(command.storeId)
        val productImport = execute(command, store)
        productImportService.notify(productImport)
    }

    private fun execute(command: ImportProductCommand, store: StoreEntity): ProductImportEntity {
        val errors = mutableListOf<ImportError>()
        val path = "product/import/" +
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" +
            "store/${command.storeId}/" +
            UUID.randomUUID()
        logger.add("import_path", path)

        val result = try {
            val tmp = import(store, command.url, path, errors)
            val products = service.unpublishOthers(store, tmp.externalIds)

            tmp.copy(unpublishedCount = products.size)
        } catch (ex: Exception) {
            errors.add(ImportError(0, ErrorCode.PRODUCT_IMPORT_FAILED))
            ImportResult(
                url = command.url,
                errors = errors
            )
        }

        logger.add("error_count", errors.size)

        // Store Errors
        return productImportService.onImported(path, store, result)
    }

    @Transactional
    fun onImported(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val store = storeService.findById(event.entityId)
        storeService.onProductsImported(store)
    }

    private fun import(store: StoreEntity, url: String, path: String, errors: MutableList<ImportError>): ImportResult {
        // Download the file
        val file = File.createTempFile("import", ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            storage.get(URL(url), fout)
        }

        // Read CSV
        val externalIds = mutableListOf<String>()
        var imported = 0
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .setHeader(
                    "id",
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
                )
            }
        product.price = record.get("price").toLong()
        product.available = (record.get("availability")?.trim()?.lowercase() == "in stock")
        product.description = record.get("description")?.trim()
        product.title = record.get("title").trim()

        try {
            product.fileUrl = downloadFile(record.get("file_link"), path)
        } catch (ex: Exception) {
            errors.add(
                ImportError(row, ErrorCode.PRODUCT_FILE_LINK_UNABLE_TO_DOWNLOAD)
            )
        }

        try {
            product.imageUrl = downloadImage(record.get("image_link"), path)
        } catch (ex: Exception) {
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

    fun downloadImage(link: String, path: String): String? {
        val url = URL(link)
        if (storage.contains(url)) {
            return link
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
                return storage.store("$path/${file.name}", input).toString()
            }
        } finally {
            out.close()
        }
    }

    private fun downloadFile(link: String, path: String): String {
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
            url.openStream().use {
                IOUtils.copy(it, fout)
            }
        }

        // Store to the cloud
        val input = FileInputStream(file)
        input.use {
            return storage.store("$path/${file.name}", input).toString()
        }
    }
}
