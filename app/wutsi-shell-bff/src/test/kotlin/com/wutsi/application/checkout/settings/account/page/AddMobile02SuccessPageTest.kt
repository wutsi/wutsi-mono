package com.wutsi.application.checkout.settings.account.page

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.checkout.settings.account.entity.AccountEntity
import com.wutsi.enums.PaymentMethodType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class AddMobile02SuccessPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = AccountEntity(
        number = "+237670000010",
        type = PaymentMethodType.MOBILE_MONEY.name,
        ownerName = "Ray Sponsible",
        otpToken = "1111",
        providerId = 1L,
    )

    private fun url() = "http://localhost:$port${Page.getSettingsAccountUrl()}/add/mobile/pages/success"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, AccountEntity::class.java)
    }

    @Test
    fun index() {
        assertEndpointEquals("/checkout/settings/account/pages/add-mobile-success.json", url())
    }
}
