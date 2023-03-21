package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.SaveAccountDeviceRequest
import com.wutsi.membership.access.service.DeviceService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class SaveAccountDeviceDelegate(private val service: DeviceService) {
    @Transactional
    public fun invoke(id: Long, request: SaveAccountDeviceRequest) {
        service.save(id, request)
    }
}
