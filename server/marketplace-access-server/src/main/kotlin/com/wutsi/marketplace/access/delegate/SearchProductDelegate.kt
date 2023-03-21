package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.SearchProductRequest
import com.wutsi.marketplace.access.dto.SearchProductResponse
import com.wutsi.marketplace.access.service.ProductService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class SearchProductDelegate(
    private val service: ProductService,
    private val httpRequest: HttpServletRequest,
    private val logger: KVLogger,
) {
    fun invoke(request: SearchProductRequest): SearchProductResponse {
        logger.add("request_offset", request.offset)
        logger.add("request_limit", request.limit)
        logger.add("request_sort_by", request.sortBy)
        logger.add("request_status", request.status)
        logger.add("request_product_ids", request.productIds)
        logger.add("request_category_ids", request.categoryIds)
        logger.add("request_store_id", request.storeId)

        val language = httpRequest.getHeader("Accept-Language")
        logger.add("language", language)

        val products = service.search(request)
        logger.add("response_count", products.size)
        return SearchProductResponse(
            products = products.map { service.toProductSummary(it, language) },
        )
    }
}
