package com.wutsi.application.common.endpoint

import com.wutsi.application.util.SecurityUtil
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.Member
import com.wutsi.security.manager.SecurityManagerApi
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractSecuredEndpoint : AbstractEndpoint() {
    @Autowired
    protected lateinit var membershipManagerApi: MembershipManagerApi

    @Autowired
    protected lateinit var securityManagerApi: SecurityManagerApi

    fun getCurrentMemberId(): Long =
        SecurityUtil.getMemberId()

    fun getCurrentMember(): Member =
        membershipManagerApi.getMember(getCurrentMemberId()).member
}
