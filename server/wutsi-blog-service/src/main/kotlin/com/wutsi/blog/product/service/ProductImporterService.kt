package com.wutsi.blog.product.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.service.WalletService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.service.UserService
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.apache.commons.io.IOUtils
import org.aspectj.weaver.tools.cache.SimpleCacheFactory.path
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.jvm.optionals.getOrElse

@Service
class ProductImporterService(
    private val service: ProductService,
    private val storage: StorageService,
    private val logger: KVLogger,
    private val userService: UserService,
    private val walletService: WalletService,
    private val objectMapper: ObjectMapper,
    private val validator: ProductImporterValidator,
    private val eventStore: EventStore,
) {
    fun import(command: ImportProductCommand) {
        execute(command)
        service.notifyImport(command)
    }

    private fun execute(command: ImportProductCommand) {
        logger.add("command_user_id", command.userId)
        logger.add("command_timestamp", command.timestamp)
        logger.add("command_url", command.url)

        // User
        val user = userService.findById(command.userId)
        val errors = validator.validate(user).toMutableList()
        if (errors.isEmpty()) {
            // Import
            val path = "product/import/" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" +
                "user/${command.userId}/" +
                UUID.randomUUID()
            try {
                val imported = import(user, command.url, path, errors)

                logger.add("import_path", path)
                logger.add("import_count", imported)
            } catch (ex: Exception) {
                errors.add(ImportError(0, ErrorCode.PRODUCT_IMPORT_FAILED))
            }
        }
        logger.add("error_count", errors.size)

        // Store Errors
        if (errors.isNotEmpty()) {
            val txt = objectMapper.writeValueAsString(mapOf("errors" to errors))
            storage.store("$path/errors.json", ByteArrayInputStream(txt.toByteArray()))
        }
    }

    @Transactional
    fun onImported(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val userId = event.entityId.toLong()
        userService.onProductImported(userId)
    }

    private fun import(user: UserEntity, url: String, path: String, errors: MutableList<ImportError>): Long {
        val wallet = getWallet(user)
        if (wallet == null) {
            errors.add(
                ImportError(
                    0,
                    ErrorCode.WALLET_NOT_FOUND
                )
            )
            return 0
        }

        // Download the file
        val file = File.createTempFile("import", ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            storage.get(URL(url), fout)
        }

        // Read CSV
        var imported = 0L
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
                if (import(row, wallet, record, path, errors)) {
                    imported++
                }
                row++
            }
        }
        return imported
    }

    private fun getWallet(user: UserEntity): WalletEntity? =
        try {
            user.walletId?.let { walletService.findById(user.walletId!!) }
        } catch (ex: Exception) {
            null
        }

    private fun import(
        row: Int,
        wallet: WalletEntity,
        record: CSVRecord,
        path: String,
        errors: MutableList<ImportError>
    ): Boolean {
        // Validate
        val validation = validator.validate(row, record)
        if (validation.isNotEmpty()) {
            errors.addAll(validation)
            return false
        }

        val user = wallet.user
        val externalId = record.get("id")
        val product = service.findByExternalIdAndUserId(externalId, user.id!!)
            .getOrElse {
                ProductEntity(
                    userId = user.id,
                    externalId = externalId,
                    currency = wallet.currency
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
            service.save(product) // Save only when we do not have fatal errors
            true
        } else {
            false
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
