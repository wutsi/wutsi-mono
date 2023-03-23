package com.wutsi.marketplace.manager.workflow

import com.wutsi.marketplace.access.dto.Discount
import com.wutsi.marketplace.manager.dto.CreateDiscountRequest
import com.wutsi.marketplace.manager.dto.CreateDiscountResponse
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.Rule
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.rule.account.DiscountShouldHaveStartDateBeforeEndDateRule
import org.springframework.stereotype.Service

@Service
class CreateDiscountWorkflow(
    eventStream: EventStream,
) : AbstractDiscountWorkflow<CreateDiscountRequest, CreateDiscountResponse>(eventStream) {
    override fun getAdditionalRules(request: CreateDiscountRequest): List<Rule?> {
        val discount = Discount(
            starts = request.starts,
            ends = request.ends,
            type = request.type,
        )
        return listOf(
            DiscountShouldHaveStartDateBeforeEndDateRule(discount),
        )
    }

    override fun doExecute(
        request: CreateDiscountRequest,
        context: WorkflowContext,
    ): CreateDiscountResponse {
        val account = getCurrentAccount(context)
        val response = marketplaceAccessApi.createDiscount(
            request = com.wutsi.marketplace.access.dto.CreateDiscountRequest(
                storeId = account.storeId!!,
                name = request.name,
                starts = request.starts,
                ends = request.ends,
                allProducts = request.allProducts,
                rate = request.rate,
                type = request.type,
            ),
        )
        return CreateDiscountResponse(
            discountId = response.discountId,
        )
    }
}
