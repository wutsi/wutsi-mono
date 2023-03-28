package com.wutsi.membership.manager.delegate

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.manager.dto.CategorySummary
import com.wutsi.membership.manager.dto.SearchCategoryRequest
import com.wutsi.membership.manager.dto.SearchCategoryResponse
import org.springframework.stereotype.Service

@Service
public class SearchCategoryDelegate(private val membershipAccessApi: MembershipAccessApi) {
    public fun invoke(request: SearchCategoryRequest): SearchCategoryResponse {
        val categories = membershipAccessApi.searchCategory(
            request = com.wutsi.membership.access.dto.SearchCategoryRequest(
                keyword = request.keyword,
                categoryIds = request.categoryIds,
                limit = request.limit,
                offset = request.offset,
            ),
        ).categories

        return SearchCategoryResponse(
            categories = categories.map {
                CategorySummary(
                    id = it.id,
                    title = it.title,
                )
            },
        )
    }
}
