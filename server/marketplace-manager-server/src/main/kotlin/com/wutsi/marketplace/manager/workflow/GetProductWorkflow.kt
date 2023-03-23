package com.wutsi.marketplace.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.GetProductResponse
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.workflow.Workflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class GetProductWorkflow(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) : Workflow<Long, GetProductResponse> {
    override fun execute(productId: Long, context: WorkflowContext): GetProductResponse {
        val product = marketplaceAccessApi.getProduct(productId).product
        return GetProductResponse(
            product = objectMapper.readValue(
                objectMapper.writeValueAsString(product),
                Product::class.java,
            ),
        )
    }
}
