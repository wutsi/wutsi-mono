package com.wutsi.membership.access.entity

import java.util.Date
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_DEVICE")
data class DeviceEntity(
    @Id
    val id: Long? = null,

    var token: String = "",
    var type: String? = null,
    var model: String? = null,
    var osName: String? = null,
    var osVersion: String? = null,
    val created: Date = Date(),
    var updated: Date = Date(),
)
