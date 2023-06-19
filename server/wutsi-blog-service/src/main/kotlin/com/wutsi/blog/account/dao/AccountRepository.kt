package com.wutsi.blog.account.dao

import com.wutsi.blog.account.domain.AccountEntity
import com.wutsi.blog.account.domain.AccountProviderEntity
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AccountRepository : CrudRepository<AccountEntity, Long> {
    fun findByProviderUserIdAndProvider(
        providerUserId: String,
        provider: AccountProviderEntity,
    ): Optional<AccountEntity>

    fun findByUserAndProvider(user: UserEntity, provider: AccountProviderEntity): Optional<AccountEntity>
}
