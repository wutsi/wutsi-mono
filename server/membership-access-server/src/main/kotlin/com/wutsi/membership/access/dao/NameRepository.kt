package com.wutsi.membership.access.dao

import com.wutsi.membership.access.entity.NameEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface NameRepository : CrudRepository<NameEntity, Long> {
    fun findByValue(name: String): Optional<NameEntity>
}
