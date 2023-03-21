package com.wutsi.marketplace.access.entity

import com.wutsi.enums.ReservationStatus
import java.util.Date
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "T_RESERVATION")
data class ReservationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val orderId: String = "",

    var status: ReservationStatus = ReservationStatus.UNKNOWN,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reservation")
    var items: List<ReservationItemEntity> = emptyList(),

    val created: Date = Date(),
    val updated: Date = Date(),
    var cancelled: Date? = null,
)
