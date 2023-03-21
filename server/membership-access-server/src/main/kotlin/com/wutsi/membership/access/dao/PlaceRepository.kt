package com.wutsi.membership.access.dao

import com.wutsi.membership.access.entity.PlaceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlaceRepository : CrudRepository<PlaceEntity, Long> {
    fun findByCountry(country: String): List<PlaceEntity>
}
