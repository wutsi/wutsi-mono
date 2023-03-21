package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.EnableBusinessRequest
import com.wutsi.membership.access.service.AccountService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class EnableBusinessDelegate(private val service: AccountService) {
    @Transactional
    public fun invoke(id: Long, request: EnableBusinessRequest) {
        service.enableBusiness(id, request)
    }
}
