package com.wutsi.marketplace.access.dao

import com.wutsi.marketplace.access.entity.ReservationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : CrudRepository<ReservationEntity, Long>
