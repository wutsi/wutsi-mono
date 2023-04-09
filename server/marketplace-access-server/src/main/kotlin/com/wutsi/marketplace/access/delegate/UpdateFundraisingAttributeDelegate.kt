package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.UpdateFundraisingAttributeRequest
import com.wutsi.marketplace.access.service.FundraisingService
import org.springframework.stereotype.Service

@Service
public class UpdateFundraisingAttributeDelegate(private val service: FundraisingService) {
    public fun invoke(id: Long, request: UpdateFundraisingAttributeRequest) {
        service.updateAttribute(id, request)
    }
}
