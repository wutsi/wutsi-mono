package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.CreatePictureRequest
import com.wutsi.marketplace.manager.dto.CreatePictureResponse
import com.wutsi.marketplace.manager.workflow.CreatePictureWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class CreatePictureDelegate(
    private val workflow: CreatePictureWorkflow,
    private val logger: KVLogger,
) {
    fun invoke(request: CreatePictureRequest): CreatePictureResponse {
        logger.add("request_prodcut_id", request.productId)
        logger.add("request_url", request.url)

        return workflow.execute(request, WorkflowContext())
    }
}
