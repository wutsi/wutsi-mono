package com.wutsi.blog.product.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.error.ErrorCode.PRODUCT_FILE_LINK_INVALID
import com.wutsi.blog.error.ErrorCode.PRODUCT_FILE_LINK_MISSING
import com.wutsi.blog.error.ErrorCode.PRODUCT_ID_MISSING
import com.wutsi.blog.error.ErrorCode.PRODUCT_IMAGE_LINK_INVALID
import com.wutsi.blog.error.ErrorCode.PRODUCT_IMAGE_LINK_MISSING
import com.wutsi.blog.error.ErrorCode.PRODUCT_PRICE_INVALID
import com.wutsi.blog.error.ErrorCode.PRODUCT_PRICE_MISSING
import com.wutsi.blog.error.ErrorCode.PRODUCT_PRICE_ZERO
import com.wutsi.blog.error.ErrorCode.PRODUCT_TITLE_MISSING
import org.apache.commons.csv.CSVRecord
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class ProductImporterValidatorTest {
    private val validator = ProductImporterValidator()

    @Test
    fun `no id`() {
        val record = createCSVRecord(id = "")
        val errors = validator.validate(1, record)
        assertContainsError(1, PRODUCT_ID_MISSING, errors)
    }

    @Test
    fun `no title`() {
        val record = createCSVRecord(title = "")
        val errors = validator.validate(1, record)
        assertContainsError(1, PRODUCT_TITLE_MISSING, errors)
    }

    @Test
    fun `no file link`() {
        val record = createCSVRecord(fileLink = "")
        val errors = validator.validate(1, record)
        assertContainsError(1, PRODUCT_FILE_LINK_MISSING, errors)
    }

    @Test
    fun `bad file link`() {
        val record = createCSVRecord(fileLink = "xxx")
        val errors = validator.validate(1, record)
        assertContainsError(1, PRODUCT_FILE_LINK_INVALID, errors)
    }

    @Test
    fun `no image link`() {
        val record = createCSVRecord(imageLink = "")
        val errors = validator.validate(1, record)
        assertContainsError(1, PRODUCT_IMAGE_LINK_MISSING, errors)
    }

    @Test
    fun `bad image link`() {
        val record = createCSVRecord(imageLink = "xxx")
        val errors = validator.validate(1, record)
        assertContainsError(1, PRODUCT_IMAGE_LINK_INVALID, errors)
    }

    @Test
    fun `no price`() {
        val record = createCSVRecord(price = "")
        val errors = validator.validate(1, record)
        assertContainsError(1, PRODUCT_PRICE_MISSING, errors)
    }

    @Test
    fun `bad price`() {
        val record = createCSVRecord(price = "xx")
        val errors = validator.validate(1, record)
        assertContainsError(1, PRODUCT_PRICE_INVALID, errors)
    }

    @Test
    fun `zero price`() {
        val record = createCSVRecord(price = "0")
        val errors = validator.validate(1, record)
        assertContainsError(1, PRODUCT_PRICE_ZERO, errors)
    }

    private fun assertContainsError(
        row: Int,
        code: String,
        errors: List<ImportError>
    ) {
        assertNotNull(
            errors.find { it.row == row && it.errorCode == code }
        )
    }

    private fun createCSVRecord(
        id: String = "111",
        title: String = "Tilte",
        availability: String = "in stokck",
        imageLink: String = "https://picsum.photos/200/300",
        fileLink: String = "https://file/foo.pdf",
        description: String = "This is a description",
        price: String = "1000"
    ): CSVRecord {
        val record = mock<CSVRecord>()
        doReturn(id).whenever(record).get("id")
        doReturn(title).whenever(record).get("title")
        doReturn(availability).whenever(record).get("availability")
        doReturn(imageLink).whenever(record).get("image_link")
        doReturn(fileLink).whenever(record).get("file_link")
        doReturn(description).whenever(record).get("description")
        doReturn(price).whenever(record).get("price")
        return record
    }
}
