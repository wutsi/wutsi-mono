package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.SaveAccountDeviceRequest
import com.wutsi.membership.manager.dto.SaveDeviceRequest
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import org.springframework.stereotype.Service

@Service
class SaveDeviceWorkflow(private val membershipAccessApi: MembershipAccessApi) : Workflow {
    override fun execute(context: WorkflowContext) {
        val request = context.input as SaveDeviceRequest

        membershipAccessApi.saveAccountDevice(
            id = context.accountId!!,
            request = SaveAccountDeviceRequest(
                token = request.token,
                type = request.type,
                osVersion = request.osVersion,
                osName = request.osName,
                model = request.model,
            ),
        )
    }
}
