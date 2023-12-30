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

interface DocumentMetadataExtractor {
    fun extract(file: File, product: ProductEntity)
}
