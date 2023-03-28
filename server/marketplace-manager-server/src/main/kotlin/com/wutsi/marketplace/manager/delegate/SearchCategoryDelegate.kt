package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.CategorySummary
import com.wutsi.marketplace.manager.dto.SearchCategoryRequest
import com.wutsi.marketplace.manager.dto.SearchCategoryResponse
import org.springframework.stereotype.Service

@Service
public class SearchCategoryDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(request: SearchCategoryRequest): SearchCategoryResponse {
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
