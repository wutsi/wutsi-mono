package com.wutsi.marketplace.manager.workflow

import com.wutsi.marketplace.access.dto.Product
import com.wutsi.marketplace.access.dto.Store
import com.wutsi.marketplace.manager.dto.CreateFileResponse
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.ProductDigitalDownloadShouldNotHaveTooManyFilesRule
import org.springframework.stereotype.Service

@Service
class CreateFileWorkflow(
    eventStream: EventStream,
) : AbstractProductWorkflow<com.wutsi.marketplace.manager.dto.CreateFileRequest, CreateFileResponse>(eventStream) {
    override fun getProductId(
        request: com.wutsi.marketplace.manager.dto.CreateFileRequest,
        context: WorkflowContext,
    ) =
        request.productId

    override fun getAdditionalRules(account: Account, store: Store?, product: Product?) = listOf(
        product?.let { ProductDigitalDownloadShouldNotHaveTooManyFilesRule(it, regulationEngine) },
    )

    override fun doExecute(
        request: com.wutsi.marketplace.manager.dto.CreateFileRequest,
        context: WorkflowContext,
    ): CreateFileResponse {
        val response = marketplaceAccessApi.createFile(
            request = com.wutsi.marketplace.access.dto.CreateFileRequest(
                productId = request.productId,
                url = request.url,
                contentSize = request.contentSize,
                contentType = request.contentType,
                name = request.name,
            ),
        )
        return CreateFileResponse(
            fileId = response.fileId,
        )
    }
}
