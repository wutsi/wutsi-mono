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
import com.wutsi.marketplace.manager.dto.SearchCategoryRequest
import com.wutsi.marketplace.manager.dto.SearchCategoryResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class ProductCategory00Level0PageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0
    val productId: Long = 111L
    val categoryId: Long = 333L

    private fun url(action: String = "") =
        if (action.isNullOrEmpty()) {
            "http://localhost:$port${Page.getSettingsProductCategoryUrl()}/pages/level-0?id=$productId"
        } else {
            "http://localhost:$port${Page.getSettingsProductCategoryUrl()}/pages/level-0$action?product-id=$productId&category-id=$categoryId"
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

        // WHEN/THEN
        assertEndpointEquals("/marketplace/settings/product/pages/category-00.json", url())

        verify(marketplaceManagerApi).searchCategory(
            request = SearchCategoryRequest(
                parentId = null,
                level = 0,
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
        assertEquals("page:/${ProductCategory00Level0Page.PAGE_INDEX + 1}", action.url)

        verify(cache).put(
            DEVICE_ID,
            CategoryEntity(productId = productId, category0Id = categoryId),
        )
    }
}
