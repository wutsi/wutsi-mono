package com.wutsi.membership.access.entity

import com.wutsi.enums.PlaceType
import java.util.Date
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_PLACE")
data class PlaceEntity(
    @Id
    var id: Long = -1,
    var name: String = "",
    var nameAscii: String = "",
    var country: String = "",
    var longitude: Double? = null,
    var latitude: Double? = null,
    var timezoneId: String? = null,
    var type: PlaceType = PlaceType.UNKNOWN,
    val created: Date = Date(),
    val updated: Date = Date(),
)
