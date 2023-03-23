package com.wutsi.marketplace.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.GetStoreResponse
import com.wutsi.marketplace.manager.dto.Store
import com.wutsi.workflow.Workflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class GetStoreWorkflow(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) : Workflow<Long, GetStoreResponse> {
    override fun execute(storeId: Long, context: WorkflowContext): GetStoreResponse {
        val store = marketplaceAccessApi.getStore(storeId).store
        return GetStoreResponse(
            store = objectMapper.readValue(
                objectMapper.writeValueAsString(store),
                Store::class.java,
            ),
        )
    }
}
