package com.wutsi.membership.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import feign.FeignException
import org.springframework.stereotype.Service

@Service
class UpdateMemberAttributeWorkflow(
    private val mapper: ObjectMapper,
    private val membershipAccessApi: MembershipAccessApi,
) : Workflow {
    override fun execute(context: WorkflowContext) =
        try {
            val request = context.input as UpdateMemberAttributeRequest
            membershipAccessApi.updateAccountAttribute(
                id = context.accountId!!,
                request = UpdateAccountAttributeRequest(
                    name = request.name,
                    value = request.value,
                ),
            )
        } catch (ex: FeignException) {
            val errorResponse = toErrorResponse(ex)
            if (errorResponse?.error?.code == ErrorURN.NAME_ALREADY_ASSIGNED.urn) {
                throw ConflictException(
                    error = Error(
                        code = com.wutsi.error.ErrorURN.USERNAME_ALREADY_ASSIGNED.urn,
                    ),
                )
            } else {
                throw ex
            }
        }

    private fun toErrorResponse(ex: FeignException): ErrorResponse? =
        mapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
}
