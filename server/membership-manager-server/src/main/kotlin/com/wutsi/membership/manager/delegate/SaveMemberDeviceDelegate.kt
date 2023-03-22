package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.dto.SaveDeviceRequest
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.membership.manager.workflow.SaveDeviceWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class SaveMemberDeviceDelegate(private val workflow: SaveDeviceWorkflow) {
    public fun invoke(request: SaveDeviceRequest) {
        workflow.execute(
            WorkflowContext(
                accountId = SecurityUtil.getAccountId(),
                input = request,
            ),
        )
    }
}
