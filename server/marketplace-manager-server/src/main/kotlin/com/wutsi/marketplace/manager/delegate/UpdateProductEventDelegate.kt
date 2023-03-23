package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.UpdateProductEventRequest
import com.wutsi.marketplace.manager.workflow.UpdateProductEventWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class UpdateProductEventDelegate(
    private val workflow: UpdateProductEventWorkflow,
    private val logger: KVLogger,
) {
    public fun invoke(request: UpdateProductEventRequest) {
        logger.add("request_online", request.online)
        logger.add("request_meeting_id", request.meetingId)
        logger.add("request_meeting_password", request.meetingPassword)
        logger.add("request_meeting_provider_id", request.meetingProviderId)
        logger.add("request_starts", request.starts)
        logger.add("request_end", request.ends)

        workflow.execute(request, WorkflowContext())
    }
}
