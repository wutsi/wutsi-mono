package com.wutsi.blog.app.page.admin.store

import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.logging.KVLogger
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.net.URL

@Controller
@RequestMapping
class StoreProductPreviewController(
    private val productService: ProductService,
    private val logger: KVLogger,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    override fun pageName() = PageName.STORE_PRODUCT_PREVIEW

    @GetMapping("/me/store/products/{id}/preview")
    fun index(@PathVariable id: Long, model: Model): String {
        val product = productService.get(id)
        model.addAttribute("product", product)
        model.addAttribute("returnUrl", "/me/store/products/${product.id}")

        return "admin/store/product/preview"
    }

    @GetMapping("/me/store/products/{id}/preview/content.epub")
    fun epub(@PathVariable id: Long, response: HttpServletResponse) {
        val product = productService.get(id)

        if (!product.streamable) {
            response.sendError(404, "Not streamable")
        } else {
            response.contentType = product.fileContentType
            val input = URL(product.fileUrl).openStream()
            input.use {
                IOUtils.copy(input, response.outputStream)
            }
        }
    }

    @GetMapping("/me/store/products/{id}/preview/pages/{number}")
    fun page(@PathVariable id: Long, @PathVariable number: Int, response: HttpServletResponse) {
        val page = productService.page(id, number)
        response.contentType = page.contentType
        val input = URL(page.contentUrl).openStream()
        input.use {
            IOUtils.copy(input, response.outputStream)
        }
    }
}
