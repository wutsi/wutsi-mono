package com.wutsi.marketplace.access.dao

import com.wutsi.marketplace.access.entity.ReservationEntity
import com.wutsi.marketplace.access.entity.ReservationItemEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationItemRepository : CrudRepository<ReservationItemEntity, Long> {
    fun findByReservation(reservation: ReservationEntity): List<ReservationItemEntity>
}
