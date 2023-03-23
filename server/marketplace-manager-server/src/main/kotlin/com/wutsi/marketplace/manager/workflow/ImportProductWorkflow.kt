package com.wutsi.marketplace.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.dto.ProductSummary
import com.wutsi.marketplace.access.dto.SearchProductRequest
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.marketplace.manager.dto.CreateProductRequest
import com.wutsi.marketplace.manager.dto.ImportProductRequest
import com.wutsi.marketplace.manager.dto.ProductAttribute
import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import com.wutsi.marketplace.manager.util.ProductHandleGenerator
import com.wutsi.marketplace.manager.util.csv.CsvError
import com.wutsi.marketplace.manager.util.csv.CsvImportResponse
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import feign.FeignException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.net.URL

@Service
class ImportProductWorkflow(
    private val mapper: ObjectMapper,
    private val createProductWorkflow: CreateProductWorkflow,
    private val updateProductAttributeWorkflow: UpdateProductAttributeWorkflow,
    private val publishProductWorkflow: PublishProductWorkflow,

    eventStream: EventStream,
) : AbstractProductWorkflow<ImportProductRequest, CsvImportResponse>(eventStream) {
    companion object {
        const val COLUMN_TITLE = "title"
        const val COLUMN_SUMMARY = "summary"
        const val COLUMN_PRICE = "price"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_PUBLISH = "publish"
        const val COLUMN_PICTURE_URL = "picture_url"

        val ATTRIBUTE_TO_COLUMN_MAPPING = mapOf(
            "title" to COLUMN_TITLE,
            "summary" to COLUMN_SUMMARY,
            "price" to COLUMN_PRICE,
            "category-id" to COLUMN_CATEGORY,
            "quantity" to COLUMN_QUANTITY,
        )
    }

    override fun getProductId(request: ImportProductRequest, context: WorkflowContext): Long? =
        null

    override fun doExecute(request: ImportProductRequest, context: WorkflowContext): CsvImportResponse {
        var row = 1
        var imported = 0
        val errors = mutableListOf<CsvError>()
        val parser = CSVParser.parse(
            URL(request.url),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(
                    COLUMN_TITLE,
                    COLUMN_SUMMARY,
                    COLUMN_PRICE,
                    COLUMN_CATEGORY,
                    COLUMN_QUANTITY,
                    COLUMN_PUBLISH,
                    COLUMN_PICTURE_URL,
                )
                .build(),
        )
        parser.use {
            val products = loadProducts(context)
            for (record in parser) {
                val logger = DefaultKVLogger()
                logger.add("row", row)

                try {
                    val error = validate(row, record)
                    if (error == null) {
                        doImport(record, products, context)
                        logger.add("record_title", record.get(COLUMN_TITLE))
                        imported++
                    } else {
                        handleError(error, errors, logger)
                    }
                } catch (ex: FeignException) {
                    val error = toCsvError(row, ex)
                    handleError(error, errors, logger)
                } finally {
                    logger.log()
                    row++
                }
            }

            return CsvImportResponse(
                imported = imported,
                errors = errors,
            )
        }
    }

    private fun handleError(error: CsvError, errors: MutableList<CsvError>, logger: KVLogger) {
        if (error.code == ErrorURN.CATEGORY_NOT_FOUND.urn) {
            errors.add(error.copy(description = "Category is not valid"))
        } else {
            errors.add(error)
        }
        log(error, logger)
    }

    private fun validate(row: Int, record: CSVRecord): CsvError? =
        if (record.get(COLUMN_TITLE).isNullOrEmpty()) {
            CsvError(row, description = "Title is required")
        } else if (!isNumeric(record.get(COLUMN_PRICE))) {
            CsvError(row, description = "Price should be a numeric value")
        } else if (!isNumeric(record.get(COLUMN_QUANTITY))) {
            CsvError(row, description = "Quantity should be a numeric value")
        } else if (!isNumeric(record.get(COLUMN_CATEGORY))) {
            CsvError(row, description = "Category should be a numeric value")
        } else {
            null
        }

    private fun doImport(record: CSVRecord, products: Map<String, ProductSummary>, context: WorkflowContext) {
        // Import product
        val title = record.get(COLUMN_TITLE)
        val handle = ProductHandleGenerator.generate(title)
        val product = products[handle]
        val productId = if (product == null) {
            create(record, context)
        } else {
            update(record, product, context)
        }

        // Publish
        if (record.get(COLUMN_PUBLISH).equals("true", true) && product?.status != ProductStatus.PUBLISHED.name) {
            publish(productId, context)
        }
    }

    private fun create(record: CSVRecord, context: WorkflowContext): Long =
        createProductWorkflow.execute(
            request = CreateProductRequest(
                title = record.get(COLUMN_TITLE),
                categoryId = toLong(record.get(COLUMN_CATEGORY)),
                summary = record.get(COLUMN_SUMMARY),
                price = toLong(record.get(COLUMN_PRICE)),
                quantity = toInt(record.get(COLUMN_QUANTITY)),
            ),
            context = context,
        ).productId

    private fun update(record: CSVRecord, product: ProductSummary, context: WorkflowContext): Long {
        val attributes = mutableListOf<ProductAttribute>()
        ATTRIBUTE_TO_COLUMN_MAPPING.forEach {
            val value = record.get(it.value)
            if (!value.isNullOrEmpty()) {
                attributes.add(ProductAttribute(it.key, value))
            }
        }
        updateProductAttributeWorkflow.execute(
            request = UpdateProductAttributeListRequest(
                attributes = attributes,
                productId = product.id,
            ),
            context = context,
        )
        return product.id
    }

    private fun publish(productId: Long, context: WorkflowContext) {
        publishProductWorkflow.execute(productId, context)
    }

    private fun toLong(value: String?): Long? =
        if (value.isNullOrEmpty()) null else value.toLong()

    private fun toInt(value: String?): Int? =
        if (value.isNullOrEmpty()) null else value.toInt()

    private fun isNumeric(value: String): Boolean {
        if (value.isNullOrEmpty()) {
            return true
        } else {
            try {
                value.toLong()
                return true
            } catch (ex: Exception) {
                return false
            }
        }
    }

    private fun loadProducts(context: WorkflowContext): Map<String, ProductSummary> =
        marketplaceAccessApi.searchProduct(
            request = SearchProductRequest(
                storeId = getCurrentAccount(context).storeId,
                limit = regulationEngine.maxProducts(),
            ),
        ).products.associateBy { ProductHandleGenerator.generate(it.title) }

    protected fun toCsvError(row: Int, ex: FeignException): CsvError =
        try {
            val response = mapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
            CsvError(
                row = row,
                code = response.error.code,
                description = response.error.message,
            )
        } catch (e: Exception) {
            CsvError(
                row = row,
                code = ex.status().toString(),
                description = ex.message,
            )
        }

    private fun log(error: CsvError, logger: KVLogger) {
        logger.add("error_code", error.code)
        logger.add("error_description", error.description)
    }

    private fun log(ex: Throwable, logger: KVLogger) {
        logger.setException(ex)
    }
}
