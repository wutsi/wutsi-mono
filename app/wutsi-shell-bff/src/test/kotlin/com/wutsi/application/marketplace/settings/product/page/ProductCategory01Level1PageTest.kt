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
import com.wutsi.marketplace.manager.dto.SearchCategoryRequest
import com.wutsi.marketplace.manager.dto.SearchCategoryResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class ProductCategory01Level1PageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0
    val productId: Long = 111L
    val categoryId: Long = 333L
    val entity = CategoryEntity(
        productId = productId,
        category0Id = 6666L,
    )

    private fun url(action: String = "") =
        if (action.isNullOrEmpty()) {
            "http://localhost:$port${Page.getSettingsProductCategoryUrl()}/pages/level-1?id=$productId"
        } else {
            "http://localhost:$port${Page.getSettingsProductCategoryUrl()}/pages/level-1$action?product-id=$productId&category-id=$categoryId"
        }

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, CategoryEntity::class.java)

        val categories = listOf(
            Fixtures.createProductCategorySummary(1L),
            Fixtures.createProductCategorySummary(2L),
            Fixtures.createProductCategorySummary(3L),
        )
        doReturn(SearchCategoryResponse(categories)).whenever(marketplaceManagerApi).searchCategory(any())
    }

    @Test
    fun index() {
        // GIVEN
        val category = Fixtures.createProductCategory(1L)
        doReturn(GetCategoryResponse(category)).whenever(marketplaceManagerApi).getCategory(any())

        // WHEN/THEN
        assertEndpointEquals("/marketplace/settings/product/pages/category-01.json", url())

        verify(marketplaceManagerApi).searchCategory(
            request = SearchCategoryRequest(
                parentId = entity.category0Id,
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
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/${ProductCategory01Level1Page.PAGE_INDEX + 1}", action.url)

        verify(cache).put(
            DEVICE_ID,
            CategoryEntity(
                productId = entity.productId,
                category0Id = entity.category0Id,
                category1Id = categoryId,
            ),
        )
    }
}
