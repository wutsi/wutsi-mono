package com.wutsi.marketplace.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.CategorySummary
import com.wutsi.marketplace.manager.dto.SearchCategoryRequest
import com.wutsi.marketplace.manager.dto.SearchCategoryResponse
import com.wutsi.workflow.Workflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class SearchCategoryWorkflow(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) : Workflow<SearchCategoryRequest, SearchCategoryResponse> {
    override fun execute(request: SearchCategoryRequest, context: WorkflowContext): SearchCategoryResponse {
        val response = marketplaceAccessApi.searchCategory(
            request = com.wutsi.marketplace.access.dto.SearchCategoryRequest(
                parentId = request.parentId,
                limit = request.limit,
                offset = request.offset,
                keyword = request.keyword,
                level = request.level,
                categoryIds = request.categoryIds,
            ),
        )
        return SearchCategoryResponse(
            categories = response.categories.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    CategorySummary::class.java,
                )
            },
        )
    }
}
