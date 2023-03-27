package com.wutsi.application.membership.settings.business.page

import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.membership.settings.business.dao.BusinessRepository
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.membership.manager.MembershipManagerApi
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBusinessPage : AbstractPageEndpoint() {
    @Autowired
    protected lateinit var dao: BusinessRepository

    @Autowired
    protected lateinit var membershipManagerApi: MembershipManagerApi

    @Autowired
    protected lateinit var checkoutManagerApi: CheckoutManagerApi
}
