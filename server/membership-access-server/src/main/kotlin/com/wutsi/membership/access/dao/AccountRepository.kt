package com.wutsi.membership.access.dao

import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.entity.AccountEntity
import com.wutsi.membership.access.entity.NameEntity
import com.wutsi.membership.access.entity.PhoneEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AccountRepository : CrudRepository<AccountEntity, Long> {
    fun findByPhoneAndStatus(
        phone: PhoneEntity,
        status: AccountStatus,
    ): Optional<AccountEntity>

    fun findByName(name: NameEntity): Optional<AccountEntity>
}
