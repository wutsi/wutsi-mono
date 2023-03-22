package com.wutsi.workflow.engine

import com.wutsi.workflow.WorkflowContext

interface Workflow {
    fun execute(context: WorkflowContext)
}
