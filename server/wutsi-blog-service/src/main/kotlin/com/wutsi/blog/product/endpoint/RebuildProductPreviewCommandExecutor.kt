package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class RebuildProductPreviewCommandExecutor(
    private val service: ProductService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RebuildProductPreviewCommandExecutor::class.java)
    }

    @GetMapping("/v1/products/commands/rebuild-preview")
    fun execute() {
        var offset = 0
        var count = 0
        while (true) {
            val products = service.searchProducts(
                SearchProductRequest(
                    offset = offset,
                    limit = 100
                )
            )
            if (products.isEmpty()) {
                return
            }

            products.forEach { product ->
                try {
                    if (service.generatePreview(product)) {
                        count++
                    }
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to generate preview for Product#${product.id}", ex)
                }
            }
            offset += products.size
        }
        LOGGER.info("$count preview(s) generated")
    }
}
