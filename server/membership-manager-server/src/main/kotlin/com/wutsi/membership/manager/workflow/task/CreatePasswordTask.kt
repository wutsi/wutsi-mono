package com.wutsi.membership.manager.workflow.task

import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.CreatePasswordRequest
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class CreatePasswordTask(
    private val workflowEngine: WorkflowEngine,
    private val securityManagerApi: SecurityManagerApi,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("membership-manager", "create-password")
        const val CONTEXT_ATTR_USERNAME = "username"
        const val CONTEXT_ATTR_PASSWORD = "password"
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        securityManagerApi.createPassword(
            CreatePasswordRequest(
                accountId = context.accountId!!,
                username = context.data[CONTEXT_ATTR_USERNAME] as String,
                value = context.data[CONTEXT_ATTR_PASSWORD] as String,
            ),
        )
    }
}
