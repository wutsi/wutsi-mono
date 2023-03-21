package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.GetProductResponse
import com.wutsi.marketplace.access.service.ProductService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class GetProductDelegate(
    private val service: ProductService,
    private val httpRequest: HttpServletRequest,
    private val logger: KVLogger,
) {
    fun invoke(id: Long): GetProductResponse {
        val language = httpRequest.getHeader("Accept-Language")
        logger.add("language", language)

        val product = service.findById(id)
        return GetProductResponse(
            product = service.toProduct(product, language),
        )
    }
}
