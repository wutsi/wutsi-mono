package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.UpdateStorePolicyAttributeRequest
import com.wutsi.marketplace.access.service.StoreService
import org.springframework.stereotype.Service

@Service
public class UpdateStorePolicyAttributeDelegate(private val service: StoreService) {
    public fun invoke(id: Long, request: UpdateStorePolicyAttributeRequest) {
        service.updatePolicyAttribute(id, request)
    }
}
