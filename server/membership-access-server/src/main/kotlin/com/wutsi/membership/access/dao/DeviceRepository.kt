package com.wutsi.membership.access.dao

import com.wutsi.membership.access.entity.DeviceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository : CrudRepository<DeviceEntity, Long>
