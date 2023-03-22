package com.wutsi.membership.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.manager.dto.CreateMemberRequest
import com.wutsi.membership.manager.util.PhoneUtil
import com.wutsi.membership.manager.workflow.task.CreatePasswordTask
import com.wutsi.membership.manager.workflow.task.CreatePaymentMethodTask
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import feign.FeignException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class CreateMemberWorkflow(
    private val workflowEngine: WorkflowEngine,
    private val membershipAccessApi: MembershipAccessApi,
    private val objectMapper: ObjectMapper,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("membership-manager", "create-member")
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        val request = context.input as CreateMemberRequest
        val accountId = createAccount(request)
        createPassword(accountId, request)
        createPaymentMethod(accountId)
    }

    private fun createAccount(request: CreateMemberRequest): Long =
        try {
            membershipAccessApi.createAccount(
                request = CreateAccountRequest(
                    phoneNumber = request.phoneNumber,
                    displayName = request.displayName,
                    country = PhoneUtil.detectCountry(request.phoneNumber),
                    language = LocaleContextHolder.getLocale().language,
                    cityId = request.cityId,
                ),
            ).accountId
        } catch (ex: FeignException) {
            val errorResponse = toErrorResponse(ex)
            if (errorResponse.error.code == ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn) {
                throw ConflictException(
                    error = Error(
                        code = com.wutsi.error.ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn,
                        data = mapOf(
                            "phone-number" to request.phoneNumber,
                        ),
                    ),
                )
            } else {
                throw ex
            }
        }

    private fun createPassword(accountId: Long, request: CreateMemberRequest) =
        workflowEngine.executeAsync(
            CreatePasswordTask.ID,
            WorkflowContext(
                accountId = accountId,
                data = mutableMapOf(
                    CreatePasswordTask.CONTEXT_ATTR_USERNAME to request.phoneNumber,
                    CreatePasswordTask.CONTEXT_ATTR_PASSWORD to request.pin,
                ),
            ),
        )

    private fun createPaymentMethod(accountId: Long) =
        workflowEngine.executeAsync(CreatePaymentMethodTask.ID, WorkflowContext(accountId = accountId))

    private fun toErrorResponse(ex: FeignException): ErrorResponse =
        objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
}
