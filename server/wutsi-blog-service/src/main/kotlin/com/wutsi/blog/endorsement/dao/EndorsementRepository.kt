package com.wutsi.blog.endorsement.dao

import com.wutsi.blog.endorsement.domain.EndorsementEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EndorsementRepository : CrudRepository<EndorsementEntity, Long> {
    fun findByUserIdAndEndorserId(userId: Long, endorserId: Long): EndorsementEntity?
}
