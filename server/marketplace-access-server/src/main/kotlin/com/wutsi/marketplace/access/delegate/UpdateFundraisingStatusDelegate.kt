package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.UpdateFundraisingStatusRequest
import com.wutsi.marketplace.access.service.FundraisingService
import org.springframework.stereotype.Service

@Service
public class UpdateFundraisingStatusDelegate(private val service: FundraisingService) {
    public fun invoke(id: Long, request: UpdateFundraisingStatusRequest) {
        service.updateStatus(id, request)
    }
}
