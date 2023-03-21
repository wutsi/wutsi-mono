package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.GetAccountDeviceResponse
import com.wutsi.membership.access.service.DeviceService
import org.springframework.stereotype.Service

@Service
public class GetAccountDeviceDelegate(private val service: DeviceService) {
    public fun invoke(id: Long): GetAccountDeviceResponse {
        val device = service.findByAccountId(id)
        return GetAccountDeviceResponse(
            device = service.toDevice(device),
        )
    }
}
