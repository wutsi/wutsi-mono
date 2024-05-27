package com.wutsi.blog.app.page.store

import com.wutsi.blog.app.service.ProductService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class LiretamaController(
    @Value("\${wutsi.liretama.affiliate-id}") private val affiliateId: String,
    private val productService: ProductService,
) {
    @GetMapping("/liretama/{id}")
    fun index(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val product = productService.get(id)

        return if (product.liretamaUrl.isNullOrEmpty()) {
            "redirect:/product/$id"
        } else {
            "redirect:${product.liretamaUrl}?pid=$affiliateId"
        }
    }
}
