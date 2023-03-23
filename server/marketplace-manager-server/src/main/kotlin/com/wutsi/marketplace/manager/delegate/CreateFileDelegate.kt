package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.CreateFileRequest
import com.wutsi.marketplace.manager.dto.CreateFileResponse
import com.wutsi.marketplace.manager.workflow.CreateFileWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class CreateFileDelegate(
    private val logger: KVLogger,
    private val workflow: CreateFileWorkflow,
) {
    public fun invoke(request: CreateFileRequest): CreateFileResponse {
        logger.add("request_url", request.url)
        logger.add("request_product_id", request.productId)
        logger.add("request_content_size", request.contentSize)
        logger.add("request_content_type", request.contentType)

        val response = workflow.execute(request, WorkflowContext())
        logger.add("response_file_id", response.fileId)

        return CreateFileResponse(
            fileId = response.fileId,
        )
    }
}
