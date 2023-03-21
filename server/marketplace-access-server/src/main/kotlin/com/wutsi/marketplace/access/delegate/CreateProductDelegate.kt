package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.CreateProductRequest
import com.wutsi.marketplace.access.dto.CreateProductResponse
import com.wutsi.marketplace.access.service.ProductService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class CreateProductDelegate(
    private val service: ProductService,
    private val logger: KVLogger,
) {
    @Transactional
    public fun invoke(request: CreateProductRequest): CreateProductResponse {
        logger.add("request_picture_url", request.pictureUrl)
        logger.add("request_title", request.title)
        logger.add("request_summary", request.summary)
        logger.add("request_price", request.price)
        logger.add("request_category_id", request.categoryId)
        logger.add("request_store_id", request.storeId)

        // Create product
        val product = service.create(request)
        logger.add("product_id", product.id)

        return CreateProductResponse(
            productId = product.id ?: -1,
        )
    }
}
