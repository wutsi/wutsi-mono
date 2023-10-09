package com.wutsi.blog.enforsement.dao

import com.wutsi.blog.enforsement.domain.EndorsementEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EndorsementRepository : CrudRepository<EndorsementEntity, Long> {
    fun findByUserIdAndEndorserId(userId: Long, endorserId: Long): EndorsementEntity?
}
