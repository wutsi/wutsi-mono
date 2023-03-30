package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.CreateFundraisingRequest
import com.wutsi.marketplace.access.dto.CreateFundraisingResponse
import com.wutsi.marketplace.access.service.FundraisingService
import org.springframework.stereotype.Service

@Service
public class CreateFundraisingDelegate(private val service: FundraisingService) {
    public fun invoke(request: CreateFundraisingRequest): CreateFundraisingResponse {
        val fundraising = service.create(request)
        return CreateFundraisingResponse(
            fundraisingId = fundraising.id ?: -1,
        )
    }
}
