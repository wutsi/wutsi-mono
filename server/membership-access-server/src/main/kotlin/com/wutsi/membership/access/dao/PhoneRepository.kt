package com.wutsi.membership.access.dao

import com.wutsi.membership.access.entity.PhoneEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PhoneRepository : CrudRepository<PhoneEntity, Long> {
    fun findByNumber(number: String): Optional<PhoneEntity>
}
