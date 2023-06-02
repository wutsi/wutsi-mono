package com.wutsi.blog.account.dao

import com.wutsi.blog.account.domain.Account
import com.wutsi.blog.account.domain.AccountProvider
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AccountRepository : CrudRepository<Account, Long> {
    fun findByProviderUserIdAndProvider(providerUserId: String, provider: AccountProvider): Optional<Account>
    fun findByUserAndProvider(user: UserEntity, provider: AccountProvider): Optional<Account>
}
