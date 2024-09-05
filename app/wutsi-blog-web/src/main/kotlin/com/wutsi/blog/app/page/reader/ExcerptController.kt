package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductType
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.net.URL

@Controller
class ExcerptController(
    private val productService: ProductService,
    private val userService: UserService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.EXCERPT

    @GetMapping("/excerpt/{id}")
    fun index(
        @PathVariable id: Long,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        @RequestParam(name = "buy", required = false) buy: String? = null,
        model: Model,
    ): String =
        index2(id, "", returnUrl, buy, model)

    @GetMapping("/excerpt/{id}/{title}")
    fun index2(
        @PathVariable id: Long,
        @PathVariable title: String,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        @RequestParam(name = "buy", required = false) buy: String? = null,
        model: Model,
    ): String {
        val product = productService.get(id)
        val store = storeService.get(product.storeId)
        val blog = userService.get(store.userId)

        model.addAttribute("product", product)
        model.addAttribute("blog", blog)
        model.addAttribute("returnUrl", (returnUrl ?: "/product/$id"))
        model.addAttribute("buy", buy)
        model.addAttribute("page", toPage(product, blog))

        return "store/excerpt"
    }

    @GetMapping("/excerpt/{id}/content.epub")
    fun content(@PathVariable id: Long, response: HttpServletResponse) {
        val product = productService.get(id)

        if (product.previewUrl == null) {
            response.sendError(404)
        } else {
            val input = URL(product.previewUrl).openStream()
            input.use {
                response.contentType = product.fileContentType
                IOUtils.copy(input, response.outputStream)
            }
        }
    }

    private fun toPage(product: ProductModel, blog: UserModel) = createPage(
        description = product.description ?: "",
        title = product.title + " - " + requestContext.getMessage("label.excerpt"),
        url = product.url,
        imageUrl = if (product.type == ProductType.EBOOK || product.type == ProductType.COMICS) {
            "$baseUrl/product/${product.id}/image.png"
        } else {
            product.imageUrl
        },
        type = if (product.type == ProductType.EBOOK || product.type == ProductType.COMICS) "book" else "website",
        author = blog.fullName,
        tags = product.category
            ?.longTitle
            ?.split(">")
            ?.map { category -> category.trim() }
            ?.toList()
            ?: emptyList()
    )
}
