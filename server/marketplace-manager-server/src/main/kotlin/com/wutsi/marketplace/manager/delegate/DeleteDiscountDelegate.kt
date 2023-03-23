package com.wutsi.marketplace.manager.delegate

import com.wutsi.marketplace.manager.workflow.DeleteDiscountWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class DeleteDiscountDelegate(private val workflow: DeleteDiscountWorkflow) {
    public fun invoke(id: Long) {
        workflow.execute(id, WorkflowContext())
    }
}
