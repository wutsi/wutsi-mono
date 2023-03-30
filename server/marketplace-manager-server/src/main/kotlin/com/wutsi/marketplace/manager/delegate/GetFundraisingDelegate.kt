package com.wutsi.marketplace.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.Fundraising
import com.wutsi.marketplace.manager.dto.GetFundraisingResponse
import org.springframework.stereotype.Service

@Service
public class GetFundraisingDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(id: Long): GetFundraisingResponse {
        val fundraising = marketplaceAccessApi.getFundraising(id).fundraising
        return GetFundraisingResponse(
            fundraising = objectMapper.readValue(
                objectMapper.writeValueAsString(fundraising),
                Fundraising::class.java,
            ),
        )
    }
}
