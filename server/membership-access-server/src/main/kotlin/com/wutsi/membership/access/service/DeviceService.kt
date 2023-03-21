package com.wutsi.membership.access.service

import com.wutsi.membership.access.dao.DeviceRepository
import com.wutsi.membership.access.dto.Device
import com.wutsi.membership.access.dto.SaveAccountDeviceRequest
import com.wutsi.membership.access.entity.DeviceEntity
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.ZoneOffset
import java.util.Date

@Service
class DeviceService(private val dao: DeviceRepository) {
    fun findByAccountId(id: Long): DeviceEntity =
        dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.DEVICE_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }

    fun save(id: Long, request: SaveAccountDeviceRequest): DeviceEntity {
        val device = dao.findById(id)
            .orElse(DeviceEntity(id = id))

        device.token = request.token
        device.type = request.type
        device.model = request.model
        device.osName = request.osName
        device.osVersion = request.osVersion
        device.updated = Date()
        return dao.save(device)
    }

    fun toDevice(device: DeviceEntity) = Device(
        token = device.token,
        osName = device.osName,
        osVersion = device.osVersion,
        type = device.type,
        model = device.model,
        created = device.created.toInstant().atOffset(ZoneOffset.UTC),
        updated = device.updated.toInstant().atOffset(ZoneOffset.UTC),
    )
}
