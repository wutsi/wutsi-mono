package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.CreateProductRequest
import com.wutsi.marketplace.manager.dto.CreateProductResponse
import com.wutsi.marketplace.manager.workflow.CreateProductWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class CreateProductDelegate(
    private val workflow: CreateProductWorkflow,
    private val logger: KVLogger,
) {
    fun invoke(request: CreateProductRequest): CreateProductResponse {
        logger.add("request_picture_url", request.pictureUrl)
        logger.add("request_title", request.title)
        logger.add("request_summary", request.summary)
        logger.add("request_price", request.price)
        logger.add("request_category_id", request.categoryId)

        return workflow.execute(request, WorkflowContext())
    }
}
