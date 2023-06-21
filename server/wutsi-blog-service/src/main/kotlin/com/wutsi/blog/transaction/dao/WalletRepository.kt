package com.wutsi.blog.transaction.dao

import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface WalletRepository : CrudRepository<WalletEntity, String> {
    fun findByUser(user: UserEntity): Optional<WalletEntity>
}
