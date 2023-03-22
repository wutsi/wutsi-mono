package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.workflow.ImportCategoryWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class ImportCategoryDelegate(
    private val workflow: ImportCategoryWorkflow,
) {
    fun invoke(language: String) {
        workflow.execute(WorkflowContext(input = language))
    }
}
