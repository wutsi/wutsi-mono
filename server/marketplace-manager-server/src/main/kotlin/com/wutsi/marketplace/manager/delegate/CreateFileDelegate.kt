package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.Product
import com.wutsi.marketplace.manager.dto.CreateFileRequest
import com.wutsi.marketplace.manager.dto.CreateFileResponse
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.regulation.RegulationEngine
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.ProductDigitalDownloadShouldNotHaveTooManyFilesRule
import org.springframework.stereotype.Service

@Service
public class CreateFileDelegate(
    private val logger: KVLogger,
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val regulationEngine: RegulationEngine,
) {
    public fun invoke(request: CreateFileRequest): CreateFileResponse {
        logger.add("request_url", request.url)
        logger.add("request_product_id", request.productId)
        logger.add("request_content_size", request.contentSize)
        logger.add("request_content_type", request.contentType)

        val product = findProduct(request.productId)
        validate(product)
        val fileId = createFile(request)

        return CreateFileResponse(fileId = fileId)
    }

    private fun validate(product: Product) =
        RuleSet(
            listOf(
                ProductDigitalDownloadShouldNotHaveTooManyFilesRule(product, regulationEngine),
            ),
        ).check()

    private fun createFile(request: CreateFileRequest): Long =
        marketplaceAccessApi.createFile(
            request = com.wutsi.marketplace.access.dto.CreateFileRequest(
                productId = request.productId,
                url = request.url,
                contentSize = request.contentSize,
                contentType = request.contentType,
                name = request.name,
            ),
        ).fileId

    private fun findProduct(id: Long): Product =
        marketplaceAccessApi.getProduct(id).product
}
