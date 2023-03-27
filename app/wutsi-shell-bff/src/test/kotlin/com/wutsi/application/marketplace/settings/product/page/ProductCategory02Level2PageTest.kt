package com.wutsi.application.marketplace.settings.product.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.product.entity.CategoryEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.marketplace.manager.dto.GetCategoryResponse
import com.wutsi.marketplace.manager.dto.ProductAttribute
import com.wutsi.marketplace.manager.dto.SearchCategoryRequest
import com.wutsi.marketplace.manager.dto.SearchCategoryResponse
import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class ProductCategory02Level2PageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0
    val productId: Long = 111L
    val categoryId: Long = 333L
    val entity = CategoryEntity(
        productId = productId,
        category0Id = 6666L,
        category1Id = 7777L,
    )

    private fun url(action: String = "") =
        if (action.isNullOrEmpty()) {
            "http://localhost:$port${Page.getSettingsProductCategoryUrl()}/pages/level-2?id=$productId"
        } else {
            "http://localhost:$port${Page.getSettingsProductCategoryUrl()}/pages/level-2$action?product-id=$productId&category-id=$categoryId"
        }

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, CategoryEntity::class.java)
    }

    @Test
    fun index() {
        // GIVEN
        val categories = listOf(
            Fixtures.createProductCategorySummary(1L),
            Fixtures.createProductCategorySummary(2L),
            Fixtures.createProductCategorySummary(3L),
        )
        doReturn(SearchCategoryResponse(categories)).whenever(marketplaceManagerApi).searchCategory(any())

        val category = Fixtures.createProductCategory(1L)
        doReturn(GetCategoryResponse(category)).whenever(marketplaceManagerApi).getCategory(any())

        // WHEN/THEN
        assertEndpointEquals("/marketplace/settings/product/pages/category-02.json", url())

        verify(marketplaceManagerApi).searchCategory(
            request = SearchCategoryRequest(
                parentId = entity.category1Id,
                level = null,
                limit = AbstractProductCategoryPage.LIMIT,
            ),
        )
    }

    @Test
    fun submit() {
        // WHEN
        val response = rest.postForEntity(url("/submit"), null, Action::class.java)

        // THEN
        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)

        verify(marketplaceManagerApi).updateProductAttribute(
            request = UpdateProductAttributeListRequest(
                productId = entity.productId,
                attributes = listOf(
                    ProductAttribute(
                        name = "category-id",
                        value = categoryId.toString(),
                    ),
                ),
            ),
        )
    }
}
