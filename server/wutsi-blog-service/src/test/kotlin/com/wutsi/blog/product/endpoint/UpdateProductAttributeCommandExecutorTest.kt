package com.wutsi.blog.product.endpoint

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.UpdateProductAttributeCommand
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.storage.MimeTypes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/UpdateProductAttributeCommand.sql"])
class UpdateProductAttributeCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: ProductRepository

    @Test
    fun title() {
        val product = updateAttribute("title", "Sample product")
        assertEquals("Sample product", product.title)
    }

    @Test
    fun description() {
        val product = updateAttribute("description", "Sample description")
        assertEquals("Sample description", product.description)
    }

    @Test
    fun price() {
        val product = updateAttribute("price", "12500")
        assertEquals(12500L, product.price)
    }

    @Test
    fun imageUrl() {
        val prod = updateAttribute("image_url", "https://picsum/100/100")

        Thread.sleep(15000)
        val product = dao.findById(prod.id!!).get()
        assertNotNull(product.imageUrl)
    }

    @Test
    fun liretamaUrl() {
        val url = "https://www.liretama.com/livres/un-book-de-trop"
        val product = updateAttribute("liretama_url", url)

        assertEquals(url, product.liretamaUrl)
    }

    @Test
    fun hashtag() {
        val value = "Foo bar"
        val product = updateAttribute("hashtag", value)

        assertEquals("foo-bar", product.hashtag)
    }

    @Test
    fun `invalid liretama url`() {
        val request = UpdateProductAttributeCommand(
            productId = 1L,
            name = "liretama_url",
            value = "https://www.liretama.com/xxx/un-book-de-trop",
        )
        val response = rest.postForEntity("/v1/products/commands/update-attribute", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.PRODUCT_LIRETAMA_URL_NOT_VALID, response.body?.error?.code)
    }

    @Test
    fun fileUrlPDF() {
        val prod = updateAttribute(
            "file_url",
            "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        )

        var product = dao.findById(prod.id!!).get()
        assertEquals(MimeTypes.PDF, product.fileContentType)
        assertTrue(product.processingFile)
        assertNotNull(product.processingFileDateTime)

        Thread.sleep(15000)
        product = dao.findById(prod.id).get()
        assertEquals(MimeTypes.PDF, product.fileContentType)
        assertEquals(13264, product.fileContentLength)
        assertEquals(1, product.numberOfPages)
        assertNotNull(product.fileUrl)
        assertNull(product.previewUrl)
        assertFalse(product.processingFile)
    }

    @Test
    fun fileUrlEPUB() {
        val prod = updateAttribute(
            "file_url",
            "http://files.infogridpacific.com/ss/igp-twss.epub"
        )

        var product = dao.findById(prod.id!!).get()
        assertEquals(MimeTypes.EPUB, product.fileContentType)
        assertTrue(product.processingFile)
        assertNotNull(product.processingFileDateTime)

        Thread.sleep(15000)
        product = dao.findById(prod.id).get()
        assertEquals(MimeTypes.EPUB, product.fileContentType)
        assertEquals(181397, product.fileContentLength)
        assertNull(product.numberOfPages)
        assertNotNull(product.fileUrl)
        assertNotNull(product.previewUrl)
        assertNotNull(product.previewUrl)
        assertFalse(product.processingFile)
    }

    @Test
    fun fileUrlDOCX() {
        val prod = updateAttribute(
            "file_url",
            "https://file-examples.com/wp-content/storage/2017/02/file-sample_100kB.docx"
        )

        var product = dao.findById(prod.id!!).get()
        assertEquals(MimeTypes.DOCX, product.fileContentType)
        assertTrue(product.processingFile)
        assertNotNull(product.processingFileDateTime)

        Thread.sleep(15000)
        product = dao.findById(prod.id).get()
        assertEquals(MimeTypes.DOCX, product.fileContentType)
        assertEquals(123, product.fileContentLength)
        assertNull(product.numberOfPages)
        assertNotNull(product.fileUrl)
        assertNull(product.previewUrl)
        assertFalse(product.processingFile)
    }

    @Test
    fun fileUrlCBZ() {
        val prod = updateAttribute(
            "file_url",
            "https://github.com/afzafri/Web-Comic-Reader/raw/master/comics/whizzkids_united.cbz"
        )

        var product = dao.findById(prod.id!!).get()
        assertEquals(MimeTypes.CBZ, product.fileContentType)
        assertTrue(product.processingFile)
        assertNotNull(product.processingFileDateTime)

        Thread.sleep(15000)
        product = dao.findById(prod.id).get()
        assertEquals(MimeTypes.CBZ, product.fileContentType)
        assertEquals(4841559, product.fileContentLength)
        assertEquals(12, product.numberOfPages)
        assertNotNull(product.fileUrl)
        assertNull(product.previewUrl)
        assertFalse(product.processingFile)
    }

    @Test
    fun category() {
        val product = updateAttribute("category_id", "1000")
        assertEquals(1000L, product.category?.id)
    }

    @Test
    fun badAttribute() {
        updateAttributeWithError("xxxx", "1000", 409, ErrorCode.PRODUCT_ATTRIBUTE_INVALID)
    }

    private fun updateAttribute(name: String, value: String): ProductEntity {
        val request = UpdateProductAttributeCommand(
            productId = 1,
            name = name,
            value = value,
        )

        val response = rest.postForEntity("/v1/products/commands/update-attribute", request, Any::class.java)
        assertEquals(200, response.statusCode.value())
        return dao.findById(request.productId).get()
    }

    private fun updateAttributeWithError(name: String, value: String, statusCode: Int, code: String) {
        val request = UpdateProductAttributeCommand(
            productId = 1,
            name = name,
            value = value,
        )

        val response = rest.postForEntity("/v1/products/commands/update-attribute", request, ErrorResponse::class.java)
        assertEquals(statusCode, response.statusCode.value())

        assertEquals(code, response.body!!.error.code)
    }
}
