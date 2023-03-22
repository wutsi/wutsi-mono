package com.wutsi.workflow.engine

import com.wutsi.workflow.WorkflowContext

data class WorkflowEventPayload(
    val id: String = "",
    val context: WorkflowContext = WorkflowContext(),
)
