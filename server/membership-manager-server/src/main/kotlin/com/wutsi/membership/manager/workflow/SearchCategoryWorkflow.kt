package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.manager.dto.CategorySummary
import com.wutsi.membership.manager.dto.SearchCategoryRequest
import com.wutsi.membership.manager.dto.SearchCategoryResponse
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import org.springframework.stereotype.Service

@Service
class SearchCategoryWorkflow(private val membershipAccessApi: MembershipAccessApi) : Workflow {
    override fun execute(context: WorkflowContext) {
        val request = context.input as SearchCategoryRequest
        val categories = membershipAccessApi.searchCategory(
            request = com.wutsi.membership.access.dto.SearchCategoryRequest(
                keyword = request.keyword,
                categoryIds = request.categoryIds,
                limit = request.limit,
                offset = request.offset,
            ),
        ).categories

        context.output = SearchCategoryResponse(
            categories = categories.map {
                CategorySummary(
                    id = it.id,
                    title = it.title,
                )
            },
        )
    }
}
