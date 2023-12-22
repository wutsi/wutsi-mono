package com.wutsi.blog.product.service

import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.product.dao.ProductImportRepository
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.dao.StoreRepository
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.blog.product.dto.ProductStatus
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
class ProductImporterTest {
    @Autowired
    private lateinit var importer: ProductImporter

    @Autowired
    private lateinit var dao: ProductRepository

    @Autowired
    private lateinit var storeDao: StoreRepository

    @Autowired
    private lateinit var productImportDao: ProductImportRepository

    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var storage: StorageService

    @Test
    fun import() {
        // GIVEN
        val content = ByteArrayInputStream(
            """
                id,title,price,availability,description,image_link,file_link
                100,Product #100,1000,in stock,This is the description of product #100,https://picsum.photos/100/150,https://www.clickdimensions.com/links/TestPDFfile.pdf
                200,Product #200,1500,out of stock,This is the description of product #200,https://picsum.photos/200,https://example-files.online-convert.com/document/txt/example.txt
                300,Product with error - no price,,,This is the description of product #200,https://picsum.photos/200,https://example-files.online-convert.com/document/txt/example.txt
            """.trimIndent().toByteArray(),
        )
        val url = storage.store("product/test.csv", content)

        // WHEN
        importer.import(
            ImportProductCommand(
                storeId = "1",
                url = url.toString(),
            )
        )

        // THEN
        val store = storeDao.findById("1").get()
        val imports = productImportDao.findByStore(store)
        assertEquals(1, imports.size)
        assertEquals(url.toString(), imports[0].url)
        assertEquals(2, imports[0].importedCount)
        assertEquals(1, imports[0].errorCount)
        assertEquals(1, imports[0].unpublishedCount)
        assertNotNull(imports[0].errorReportUrl)

        val events = eventStore.events(
            streamId = StreamId.PRODUCT_IMPORT,
            entityId = imports[0].id.toString(),
            type = EventType.PRODUCT_IMPORTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        val product100 = dao.findByExternalIdAndStore("100", store).get()
        assertEquals("1", product100.store.id)
        assertEquals("100", product100.externalId)
        assertEquals("Product #100", product100.title)
        assertEquals("This is the description of product #100", product100.description)
        assertEquals(1000L, product100.price)
        assertEquals(true, product100.available)
        assertEquals(ProductStatus.PUBLISHED, product100.status)
        assertNotNull(product100.imageUrl)
        assertNotNull(product100.fileUrl)
        assertEquals("application/pdf", product100.fileContentType)
        assertEquals(83186, product100.fileContentLength)

        val product200 = dao.findByExternalIdAndStore("200", store).get()
        assertEquals(211L, product200.id)
        assertEquals("1", product100.store.id)
        assertEquals("200", product200.externalId)
        assertEquals("Product #200", product200.title)
        assertEquals("This is the description of product #200", product200.description)
        assertEquals(1500L, product200.price)
        assertEquals(false, product200.available)
        assertEquals(ProductStatus.PUBLISHED, product200.status)
        assertNotNull(product200.imageUrl)
        assertNotNull(product200.fileUrl)
        assertEquals("text/plain", product200.fileContentType)

        val product300 = dao.findByExternalIdAndStore("300", store).get()
        assertEquals(311L, product300.id)
        assertEquals("1", product100.store.id)
        assertEquals("300", product300.externalId)
        assertEquals("do-not-update-title", product300.title)
        assertEquals(null, product300.description)
        assertEquals(3000L, product300.price)
        assertEquals(true, product300.available)
        assertEquals("https://picsum/200", product300.imageUrl)
        assertEquals("https://file.com/300.pdf", product300.fileUrl)
        assertEquals(ProductStatus.DRAFT, product300.status)

        val product400 = dao.findByExternalIdAndStore("400", store).get()
        assertEquals(411L, product400.id)
        assertEquals("1", product100.store.id)
        assertEquals("400", product400.externalId)
        assertEquals("product-400", product400.title)
        assertEquals(null, product400.description)
        assertEquals(4000L, product400.price)
        assertEquals(false, product400.available)
        assertEquals("https://picsum/400/400", product400.imageUrl)
        assertEquals("https://file.com/400.pdf", product400.fileUrl)
        assertEquals(ProductStatus.DRAFT, product400.status)

        Thread.sleep(5000)
        val store2 = storeDao.findById("1").get()
        assertEquals(4L, store2.productCount)
        assertEquals(2L, store2.publishProductCount)
    }
}
