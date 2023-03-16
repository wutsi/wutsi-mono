package com.wutsi.platform.payment.provider.mtn.impl

import com.wutsi.platform.payment.provider.mtn.User
import com.wutsi.platform.payment.provider.mtn.UserProvider

class UserProviderProduction(
    userId: String,
    apiKey: String,
) : UserProvider {
    private val user = User(userId, apiKey)

    override fun get(): User =
        user
}
