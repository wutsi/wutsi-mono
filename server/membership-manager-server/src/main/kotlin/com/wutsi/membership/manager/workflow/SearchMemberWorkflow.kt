package com.wutsi.membership.manager.workflow

import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.SearchAccountRequest
import com.wutsi.membership.manager.dto.MemberSummary
import com.wutsi.membership.manager.dto.SearchMemberRequest
import com.wutsi.membership.manager.dto.SearchMemberResponse
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import org.springframework.stereotype.Service

@Service
class SearchMemberWorkflow(private val membershipAccessApi: MembershipAccessApi) : Workflow {
    override fun execute(context: WorkflowContext) {
        val request = context.input as SearchMemberRequest
        val accounts = membershipAccessApi.searchAccount(
            request = SearchAccountRequest(
                phoneNumber = request.phoneNumber,
                status = AccountStatus.ACTIVE.name,
                business = request.business,
                store = request.store,
                limit = request.limit,
                offset = request.offset,
                cityId = request.cityId,
            ),
        ).accounts

        context.output = SearchMemberResponse(
            members = accounts.map {
                MemberSummary(
                    id = it.id,
                    displayName = it.displayName,
                    pictureUrl = it.pictureUrl,
                    categoryId = it.categoryId,
                    business = it.business,
                    country = it.country,
                    cityId = it.cityId,
                    language = it.language,
                    active = it.status == AccountStatus.ACTIVE.name,
                    superUser = it.superUser,
                    name = it.name,
                )
            },
        )
    }
}
