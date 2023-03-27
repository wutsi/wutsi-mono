package com.wutsi.application.marketplace.settings.product.page

import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.product.dao.CategoryRepository
import com.wutsi.flutter.sdui.Action
import com.wutsi.marketplace.manager.dto.ProductAttribute
import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/products/category/pages/level-2")
class ProductCategory02Level0Page(
    private val dao: CategoryRepository,
) : AbstractProductCategoryPage() {
    companion object {
        const val PAGE_INDEX = 2
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getParentCategoryId(): Long? = dao.get().category1Id
    override fun getSubmitUrl() = "${Page.getSettingsProductCategoryUrl()}/pages/level-2/submit"

    @PostMapping("/submit")
    fun submit(
        @RequestParam("category-id") categoryId: Long,
    ): Action {
        val entity = dao.get()
        marketplaceManagerApi.updateProductAttribute(
            request = UpdateProductAttributeListRequest(
                productId = entity.productId,
                attributes = listOf(
                    ProductAttribute(name = "category-id", categoryId.toString()),
                ),
            ),
        )
        return gotoPreviousScreen()
    }
}
