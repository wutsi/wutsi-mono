package com.wutsi.application.marketplace.settings.product.page

import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.product.dao.CategoryRepository
import com.wutsi.application.marketplace.settings.product.entity.CategoryEntity
import com.wutsi.flutter.sdui.Action
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/products/category/pages/level-0")
class ProductCategory00Level0Page(
    private val dao: CategoryRepository,
) : AbstractProductCategoryPage() {
    companion object {
        const val PAGE_INDEX = 0
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getParentCategoryId(): Long? = null
    override fun getSubmitUrl() = "${Page.getSettingsProductCategoryUrl()}/pages/level-0/submit"

    @PostMapping("/submit")
    fun submit(
        @RequestParam("product-id") productId: Long,
        @RequestParam("category-id") categoryId: Long,
    ): Action {
        dao.save(
            CategoryEntity(
                productId = productId,
                category0Id = categoryId,
            ),
        )
        return gotoNextPage()
    }
}
