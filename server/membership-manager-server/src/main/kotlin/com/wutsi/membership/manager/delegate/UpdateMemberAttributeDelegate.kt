package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.membership.manager.workflow.UpdateMemberAttributeWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class UpdateMemberAttributeDelegate(
    private val workflow: UpdateMemberAttributeWorkflow,
    private val logger: KVLogger,
) {
    fun invoke(request: UpdateMemberAttributeRequest) {
        logger.add("request_value", request.value)
        logger.add("request_name", request.name)

        workflow.execute(
            WorkflowContext(
                accountId = SecurityUtil.getAccountId(),
                input = request,
            ),
        )
    }
}
