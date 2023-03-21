package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.CreatePictureRequest
import com.wutsi.marketplace.access.dto.CreatePictureResponse
import com.wutsi.marketplace.access.service.PictureService
import com.wutsi.marketplace.access.service.ProductService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class CreatePictureDelegate(
    private val service: PictureService,
    private val productService: ProductService,
    private val logger: KVLogger,
) {
    fun invoke(request: CreatePictureRequest): CreatePictureResponse {
        logger.add("request_product_id", request.productId)
        logger.add("request_url", request.url)

        // Picture
        val product = productService.findById(request.productId)
        val picture = service.create(product, request)

        // No thumbnail?
        if (product.thumbnail == null) {
            productService.setThumbnail(product, picture)
        }
        return CreatePictureResponse(
            pictureId = picture.id ?: -1,
        )
    }
}
