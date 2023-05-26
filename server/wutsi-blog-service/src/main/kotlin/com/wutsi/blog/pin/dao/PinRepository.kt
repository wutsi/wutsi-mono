package com.wutsi.blog.pin.dao

import com.wutsi.blog.pin.domain.Pin
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Deprecated("")
@Repository
interface PinRepository : CrudRepository<Pin, Long> {
    fun findByUserId(userId: Long): Optional<Pin>
}
