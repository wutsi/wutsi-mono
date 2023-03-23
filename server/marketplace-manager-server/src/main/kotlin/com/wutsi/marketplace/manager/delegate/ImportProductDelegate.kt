package com.wutsi.marketplace.manager.delegate

import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.marketplace.manager.dto.ImportProductRequest
import com.wutsi.marketplace.manager.workflow.ImportProductWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class ImportProductDelegate(
    private val workflow: ImportProductWorkflow,
    private val logger: KVLogger,
) {
    public fun invoke(request: ImportProductRequest) {
        logger.add("url", request.url)
        workflow.execute(
            request = request,
            context = WorkflowContext(
                accountId = SecurityUtil.getAccountId(),
            ),
        )
    }
}
