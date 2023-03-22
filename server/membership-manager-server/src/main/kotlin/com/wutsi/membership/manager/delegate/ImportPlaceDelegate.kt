package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.workflow.ImportPlaceWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class ImportPlaceDelegate(private val workflow: ImportPlaceWorkflow) {
    fun invoke(country: String) {
        workflow.execute(WorkflowContext(input = country))
    }
}
