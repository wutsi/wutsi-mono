package com.wutsi.blog.app.page.admin.store

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.SearchProductRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/store/products")
class StoreProductsController(
    private val productService: ProductService,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    companion object {
        const val LIMIT = 20
    }

    override fun pageName() = PageName.STORE_PRODUCTS

    @GetMapping
    fun index(model: Model): String {
        val store = checkStoreAccess()
        model.addAttribute("store", store)

        val products = productService.search(
            SearchProductRequest(
                storeIds = listOf(store.id),
                limit = LIMIT,
                sortBy = ProductSortStrategy.TITLE,
                sortOrder = SortOrder.ASCENDING
            )
        )
        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
        }
        return "admin/store/products"
    }
}
