package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.Product
import com.wutsi.marketplace.manager.dto.CreatePictureRequest
import com.wutsi.marketplace.manager.dto.CreatePictureResponse
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.regulation.RegulationEngine
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.ProductShouldNotHaveTooManyPicturesRule
import org.springframework.stereotype.Service

@Service
class CreatePictureDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val regulationEngine: RegulationEngine,
    private val logger: KVLogger,
) {
    fun invoke(request: CreatePictureRequest): CreatePictureResponse {
        logger.add("request_product_id", request.productId)
        logger.add("request_url", request.url)

        val product = findProduct(request.productId)
        validate(product)
        val pictureId = createPicture(request)

        return CreatePictureResponse(pictureId = pictureId)
    }

    private fun validate(product: Product) =
        RuleSet(
            listOf(
                ProductShouldNotHaveTooManyPicturesRule(product, regulationEngine),
            ),
        ).check()

    private fun createPicture(request: CreatePictureRequest): Long =
        marketplaceAccessApi.createPicture(
            request = com.wutsi.marketplace.access.dto.CreatePictureRequest(
                productId = request.productId,
                url = request.url,
            ),
        ).pictureId

    private fun findProduct(id: Long): Product =
        marketplaceAccessApi.getProduct(id).product
}
