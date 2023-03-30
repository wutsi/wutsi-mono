package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.GetFundraisingResponse
import com.wutsi.marketplace.access.service.FundraisingService
import org.springframework.stereotype.Service

@Service
public class GetFundraisingDelegate(private val service: FundraisingService) {
    public fun invoke(id: Long): GetFundraisingResponse {
        return GetFundraisingResponse(
            fundraising = service.toFundraising(
                service.findById(id),
            ),
        )
    }
}
