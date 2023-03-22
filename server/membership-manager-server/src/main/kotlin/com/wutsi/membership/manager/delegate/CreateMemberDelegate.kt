package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.dto.CreateMemberRequest
import com.wutsi.membership.manager.workflow.CreateMemberWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class CreateMemberDelegate(
    private val workflow: CreateMemberWorkflow,
    private val logger: KVLogger,
) {
    fun invoke(request: CreateMemberRequest) {
        logger.add("request_phone_number", request.phoneNumber)
        logger.add("request_country", request.country)
        logger.add("request_city_id", request.cityId)
        logger.add("request_display_name", request.displayName)

        workflow.execute(
            WorkflowContext(
                input = request,
            ),
        )
    }
}
