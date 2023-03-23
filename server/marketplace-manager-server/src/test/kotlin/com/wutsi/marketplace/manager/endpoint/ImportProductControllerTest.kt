package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.dto.ProductSummary
import com.wutsi.marketplace.access.dto.SearchProductResponse
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.marketplace.manager.dto.CreateProductRequest
import com.wutsi.marketplace.manager.dto.CreateProductResponse
import com.wutsi.marketplace.manager.dto.ImportProductRequest
import com.wutsi.marketplace.manager.dto.ProductAttribute
import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import com.wutsi.marketplace.manager.workflow.CreateProductWorkflow
import com.wutsi.marketplace.manager.workflow.PublishProductWorkflow
import com.wutsi.marketplace.manager.workflow.UpdateProductAttributeWorkflow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
class ImportProductControllerTest : AbstractProductControllerTest<ImportProductRequest>() {
    @MockBean
    private lateinit var createProductWorkflow: CreateProductWorkflow

    @MockBean
    private lateinit var updateProductAttributeWorkflow: UpdateProductAttributeWorkflow

    @MockBean
    private lateinit var publishProductWorkflow: PublishProductWorkflow

    override fun url() = "http://localhost:$port/v1/products/import"

    override fun createRequest() = ImportProductRequest(
        url = "http://localhost:$port/products.csv",
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchProductResponse()).whenever(marketplaceAccessApi).searchProduct(any())
        doReturn(CreateProductResponse(100L))
            .doReturn(CreateProductResponse(101L))
            .whenever(createProductWorkflow).execute(any(), any())
    }

    @Test
    fun create() {
        // WHEN
        val response = rest.postForEntity(url(), request, Any::class.java)
        Thread.sleep(5000)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val req = argumentCaptor<CreateProductRequest>()
        verify(createProductWorkflow, times(2)).execute(req.capture(), any())
        assertEquals("Boubou", req.firstValue.title)
        assertEquals("This is a summary", req.firstValue.summary)
        assertEquals(75000, req.firstValue.price)
        assertEquals(10000, req.firstValue.categoryId)
        assertEquals(10, req.firstValue.quantity)

        assertEquals("Veste Noire", req.secondValue.title)
        assertEquals("", req.secondValue.summary)
        assertEquals(60000, req.secondValue.price)
        assertNull(req.secondValue.categoryId)
        assertNull(req.secondValue.quantity)

        verify(updateProductAttributeWorkflow, never()).execute(any(), any())

        verify(publishProductWorkflow).execute(eq(100L), any())
        verify(publishProductWorkflow, never()).execute(eq(101L), any())

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun createWithErrors() {
        // GIVEN
        val ex = createFeignNotFoundException(ErrorURN.CATEGORY_NOT_FOUND.urn)
        doThrow(ex)
            .doReturn(CreateProductResponse(101L))
            .whenever(createProductWorkflow).execute(any(), any())

        // WHEN
        val response = rest.postForEntity(url(), request, Any::class.java)
        Thread.sleep(5000)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(createProductWorkflow, times(2)).execute(any(), any())
        verify(updateProductAttributeWorkflow, never()).execute(any(), any())

        verify(publishProductWorkflow, never()).execute(eq(100L), any())
        verify(publishProductWorkflow, never()).execute(eq(101L), any())

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun update() {
        // GIVEN
        val products = listOf(
            ProductSummary(
                id = 100,
                title = "boubou",
                price = 1,
                status = ProductStatus.DRAFT.name,
            ),
            ProductSummary(
                id = 101,
                title = "veste noire",
                price = 1,
            ),
        )
        doReturn(SearchProductResponse(products)).whenever(marketplaceAccessApi).searchProduct(any())

        // WHEN
        val response = rest.postForEntity(url(), request, Any::class.java)
        Thread.sleep(5000)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(createProductWorkflow, never()).execute(any(), any())

        val req = argumentCaptor<UpdateProductAttributeListRequest>()
        verify(updateProductAttributeWorkflow, times(2)).execute(req.capture(), any())
        assertEquals(100L, req.firstValue.productId)
        assertEquals(
            listOf(
                ProductAttribute("title", "Boubou"),
                ProductAttribute("summary", "This is a summary"),
                ProductAttribute("price", "75000"),
                ProductAttribute("category-id", "10000"),
                ProductAttribute("quantity", "10"),
            ),
            req.firstValue.attributes,
        )

        assertEquals(101L, req.secondValue.productId)
        assertEquals(
            listOf(
                ProductAttribute("title", "Veste Noire"),
                ProductAttribute("price", "60000"),
            ),
            req.secondValue.attributes,
        )

        verify(publishProductWorkflow).execute(eq(100L), any())
        verify(publishProductWorkflow, never()).execute(eq(101L), any())

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun noTitle() {
        // WHEN
        request = ImportProductRequest(
            url = "http://localhost:$port/products_no_title.csv",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)
        Thread.sleep(5000)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(createProductWorkflow, never()).execute(any(), any())

        val req = argumentCaptor<UpdateProductAttributeListRequest>()
        verify(updateProductAttributeWorkflow, never()).execute(req.capture(), any())
        verify(publishProductWorkflow, never()).execute(any(), any())

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun invalidQuantity() {
        // WHEN
        request = ImportProductRequest(
            url = "http://localhost:$port/products_invalid_quantity.csv",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)
        Thread.sleep(5000)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(createProductWorkflow, never()).execute(any(), any())

        val req = argumentCaptor<UpdateProductAttributeListRequest>()
        verify(updateProductAttributeWorkflow, never()).execute(req.capture(), any())
        verify(publishProductWorkflow, never()).execute(any(), any())

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun invalidPrice() {
        // WHEN
        request = ImportProductRequest(
            url = "http://localhost:$port/products_invalid_price.csv",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)
        Thread.sleep(5000)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(createProductWorkflow, never()).execute(any(), any())

        val req = argumentCaptor<UpdateProductAttributeListRequest>()
        verify(updateProductAttributeWorkflow, never()).execute(req.capture(), any())
        verify(publishProductWorkflow, never()).execute(any(), any())

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun invalidCategory() {
        // WHEN
        request = ImportProductRequest(
            url = "http://localhost:$port/products_invalid_category.csv",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)
        Thread.sleep(5000)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(createProductWorkflow, never()).execute(any(), any())

        val req = argumentCaptor<UpdateProductAttributeListRequest>()
        verify(updateProductAttributeWorkflow, never()).execute(req.capture(), any())
        verify(publishProductWorkflow, never()).execute(any(), any())

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    override fun notProductOwner() {
        // NOTHING
    }
}
