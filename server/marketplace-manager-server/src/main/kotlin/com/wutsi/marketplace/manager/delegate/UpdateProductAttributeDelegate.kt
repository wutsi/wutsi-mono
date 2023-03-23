package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import com.wutsi.marketplace.manager.workflow.UpdateProductAttributeWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class UpdateProductAttributeDelegate(
    private val workflow: UpdateProductAttributeWorkflow,
    private val logger: KVLogger,
) {
    public fun invoke(request: UpdateProductAttributeListRequest) {
        logger.add("request_product_id", request.productId)
        request.attributes.forEach {
            logger.add("request_attribute_${it.name}", it.value)
        }

        workflow.execute(request, WorkflowContext())
    }
}
