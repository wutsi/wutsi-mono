package com.wutsi.blog.app.page.store

import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class ShopController(
    private val productService: ProductService,
    private val userService: UserService,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    companion object {
        const val LIMIT = 20
    }

    override fun pageName() = PageName.SHOP

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    @GetMapping("/@/{name}/shop")
    fun index(@PathVariable name: String, model: Model): String {
        val blog = userService.get(name)
        val store = checkStoreAccess(blog)

        model.addAttribute("blog", blog)
        model.addAttribute("page", getPage(blog))

        val products = productService.search(
            SearchProductRequest(
                storeIds = listOf(store.id),
                limit = LIMIT,
                status = ProductStatus.PUBLISHED,
                available = true,
            )
        )
        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
        }
        return "store/shop"
    }

    private fun getPage(user: UserModel) = createPage(
        description = "",
        title = requestContext.getMessage("page.shop.metadata.title") + " | ${user.fullName}",
        url = url(user) + "/shop",
    )
}
