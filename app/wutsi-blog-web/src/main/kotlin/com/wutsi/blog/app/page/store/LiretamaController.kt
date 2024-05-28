package com.wutsi.blog.app.page.store

import com.wutsi.blog.app.service.LiretamaService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.platform.core.logging.KVLogger
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class LiretamaController(
    private val productService: ProductService,
    private val liretamaService: LiretamaService,
    private val logger: KVLogger,
) {
    @GetMapping("/liretama/{id}")
    fun index(
        @PathVariable id: Long,
        response: HttpServletResponse,
    ) {
        val product = productService.get(id)
        val redirectUrl = liretamaService.toUrl(product)

        logger.add("redirect_url", redirectUrl)
        logger.add("liretama_url", product.liretamaUrl)
        response.sendRedirect(redirectUrl)
    }
}
