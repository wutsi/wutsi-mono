package com.wutsi.marketplace.access.entity

import com.wutsi.enums.FundraisingStatus
import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_FUNDRAISING")
data class FundraisingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val accountId: Long = -1,
    val businessId: Long = -1,
    val currency: String = "",
    var status: FundraisingStatus = FundraisingStatus.UNKNOWN,
    val created: Date = Date(),
    var updated: Date = Date(),
    var deactivated: Date? = null,
)
