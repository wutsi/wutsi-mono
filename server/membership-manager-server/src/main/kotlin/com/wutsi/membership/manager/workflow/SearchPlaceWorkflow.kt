package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.manager.dto.PlaceSummary
import com.wutsi.membership.manager.dto.SearchPlaceRequest
import com.wutsi.membership.manager.dto.SearchPlaceResponse
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import org.springframework.stereotype.Service

@Service
class SearchPlaceWorkflow(private val membershipAccessApi: MembershipAccessApi) : Workflow {
    override fun execute(context: WorkflowContext) {
        val request = context.input as SearchPlaceRequest
        val places = membershipAccessApi.searchPlace(
            request = com.wutsi.membership.access.dto.SearchPlaceRequest(
                keyword = request.keyword,
                type = request.type,
                country = request.country,
                limit = request.limit,
                offset = request.offset,
            ),
        ).places

        context.output = SearchPlaceResponse(
            places = places.map {
                PlaceSummary(
                    id = it.id,
                    type = it.type,
                    name = it.name,
                    longName = it.longName,
                )
            },
        )
    }
}
