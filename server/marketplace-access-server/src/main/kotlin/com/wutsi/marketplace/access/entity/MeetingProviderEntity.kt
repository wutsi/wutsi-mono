package com.wutsi.marketplace.access.entity

import com.wutsi.enums.MeetingProviderType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_MEETING_PROVIDER")
data class MeetingProviderEntity(
    @Id
    val id: Long? = null,

    val name: String = "",
    var logoUrl: String = "",
    val type: MeetingProviderType = MeetingProviderType.ZOOM,
)
