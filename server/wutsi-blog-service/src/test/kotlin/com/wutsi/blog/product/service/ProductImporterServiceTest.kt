package com.wutsi.blog.product.service

import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/ProductImporterService.sql"])
class ProductImporterServiceTest {
    @Autowired
    private lateinit var importer: ProductImporterService

    @Autowired
    private lateinit var dao: ProductRepository

    @Autowired
    private lateinit var storage: StorageService

    @Autowired
    private lateinit var eventStore: EventStore

    @Test
    fun import() {
        // GIVEN
        val content = ByteArrayInputStream(
            """
                id,title,price,availability,description,image_link,file_link
                100,Product #100,1000,in stock,This is the description of product #100,https://picsum.photos/100/150,https://www.clickdimensions.com/links/TestPDFfile.pdf
                200,Product #200,1500,out of stock,This is the description of product #200,https://picsum.photos/200,https://example-files.online-convert.com/document/txt/example.txt
                300,Product with error - no price,,out of stock,This is the description of product #200,https://picsum.photos/200,https://example-files.online-convert.com/document/txt/example.txt
            """.trimIndent().toByteArray(),
        )
        val url = storage.store("/import/1/product.csv", content, "text/csv")

        // WHEN
        importer.import(
            ImportProductCommand(
                userId = 1,
                url = url.toString(),
            )
        )

        // THEN
        val events = eventStore.events(
            streamId = StreamId.PRODUCT,
            entityId = "1",
            type = EventType.IMPORT_PRODUCT_COMMAND,
        )
        assertTrue(events.isEmpty())

        val product100 = dao.findByExternalIdAndUserId("100", 1L).get()
        assertEquals(1L, product100.userId)
        assertEquals("100", product100.externalId)
        assertEquals("Product #100", product100.title)
        assertEquals("This is the description of product #100", product100.description)
        assertEquals(1000L, product100.price)
        assertEquals("XAF", product100.currency)
        assertEquals(true, product100.available)
        assertNotNull(product100.imageUrl)
        assertNotNull(product100.fileUrl)

        val product200 = dao.findByExternalIdAndUserId("200", 1L).get()
        assertEquals(211L, product200.id)
        assertEquals(1L, product200.userId)
        assertEquals("200", product200.externalId)
        assertEquals("Product #200", product200.title)
        assertEquals("This is the description of product #200", product200.description)
        assertEquals(1500L, product200.price)
        assertEquals("XAF", product200.currency)
        assertEquals(false, product200.available)
        assertNotNull(product200.imageUrl)
        assertNotNull(product200.fileUrl)

        val product300 = dao.findByExternalIdAndUserId("300", 1L).get()
        assertEquals(311L, product300.id)
        assertEquals(1L, product300.userId)
        assertEquals("300", product300.externalId)
        assertEquals("do-not-update-title", product300.title)
        assertEquals("do-not-update-descr", product300.description)
        assertEquals(300L, product300.price)
        assertEquals("XAF", product300.currency)
        assertEquals(true, product300.available)
        assertEquals("https://picsum/200", product300.imageUrl)
        assertEquals("https://file.com/300.pdf", product300.fileUrl)
    }
}
