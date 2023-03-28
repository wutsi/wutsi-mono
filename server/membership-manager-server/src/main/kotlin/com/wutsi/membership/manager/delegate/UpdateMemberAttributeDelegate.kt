package com.wutsi.membership.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.logging.KVLogger
import feign.FeignException
import org.springframework.stereotype.Service

@Service
class UpdateMemberAttributeDelegate(
    private val logger: KVLogger,
    private val objectMapper: ObjectMapper,
    private val membershipAccessApi: MembershipAccessApi,
) {
    fun invoke(request: UpdateMemberAttributeRequest) {
        logger.add("request_value", request.value)
        logger.add("request_name", request.name)

        updateAccount(request)
    }

    private fun updateAccount(request: UpdateMemberAttributeRequest) {
        try {
            membershipAccessApi.updateAccountAttribute(
                id = SecurityUtil.getAccountId(),
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
    }

    private fun toErrorResponse(ex: FeignException): ErrorResponse? =
        objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
}
