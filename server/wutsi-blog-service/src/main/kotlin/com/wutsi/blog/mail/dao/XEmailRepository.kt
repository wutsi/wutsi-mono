package com.wutsi.blog.mail.dao

import com.wutsi.blog.mail.domain.XEmailEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface XEmailRepository : CrudRepository<XEmailEntity, String> {
    fun findByEmail(email: String): Optional<XEmailEntity>
}
