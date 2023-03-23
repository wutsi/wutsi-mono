package com.wutsi.marketplace.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.Category
import com.wutsi.marketplace.manager.dto.GetCategoryResponse
import com.wutsi.workflow.Workflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class GetCategoryWorkflow(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) : Workflow<Long, GetCategoryResponse> {
    override fun execute(categoryId: Long, context: WorkflowContext): GetCategoryResponse {
        val discount = marketplaceAccessApi.getCategory(categoryId).category
        return GetCategoryResponse(
            category = objectMapper.readValue(
                objectMapper.writeValueAsString(discount),
                Category::class.java,
            ),
        )
    }
}
