package com.wutsi.marketplace.manager.workflow

import com.wutsi.marketplace.access.dto.Product
import com.wutsi.marketplace.access.dto.Store
import com.wutsi.marketplace.manager.dto.CreatePictureResponse
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.ProductShouldNotHaveTooManyPicturesRule
import org.springframework.stereotype.Service

@Service
class CreatePictureWorkflow(
    eventStream: EventStream,
) : AbstractProductWorkflow<com.wutsi.marketplace.manager.dto.CreatePictureRequest, CreatePictureResponse>(eventStream) {
    override fun getProductId(
        request: com.wutsi.marketplace.manager.dto.CreatePictureRequest,
        context: WorkflowContext,
    ) =
        request.productId

    override fun getAdditionalRules(account: Account, store: Store?, product: Product?) = listOf(
        product?.let { ProductShouldNotHaveTooManyPicturesRule(it, regulationEngine) },
    )

    override fun doExecute(
        request: com.wutsi.marketplace.manager.dto.CreatePictureRequest,
        context: WorkflowContext,
    ): CreatePictureResponse {
        val response = marketplaceAccessApi.createPicture(
            request = com.wutsi.marketplace.access.dto.CreatePictureRequest(
                productId = request.productId,
                url = request.url,
            ),
        )
        return CreatePictureResponse(
            pictureId = response.pictureId,
        )
    }
}
