package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.dto.SearchMemberRequest
import com.wutsi.membership.manager.dto.SearchMemberResponse
import com.wutsi.membership.manager.workflow.SearchMemberWorkflow
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class SearchMemberDelegate(
    private val workflow: SearchMemberWorkflow,
    private val logger: KVLogger,
) {
    public fun invoke(request: SearchMemberRequest): SearchMemberResponse {
        logger.add("request_phone_number", request.phoneNumber)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val context = WorkflowContext(input = request)
        workflow.execute(context)
        return context.output as SearchMemberResponse
    }
}
