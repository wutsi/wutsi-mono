package com.wutsi.blog.transaction.service

import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.platform.core.error.exception.InternalErrorException
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.provider.flutterwave.Flutterwave
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PaymentGatewayProviderTest {
    @Autowired
    private lateinit var provider: PaymentGatewayProvider

    @Test
    fun mobileMoney() {
        val gateway = provider.get(PaymentMethodType.MOBILE_MONEY)
        assertTrue(gateway is Flutterwave)
    }

    @Test
    fun none() {
        val gateway = provider.get(PaymentMethodType.NONE)
        assertTrue(gateway is NoneGateway)
    }

    @Test
    fun bank() {
        assertThrows<InternalErrorException> {
            provider.get(PaymentMethodType.BANK)
        }
    }

    @Test
    fun flutterwage() {
        val gateway = provider.get(GatewayType.FLUTTERWAVE)
        assertTrue(gateway is Flutterwave)
    }

    @Test
    fun om() {
        assertThrows<InternalErrorException> {
            provider.get(GatewayType.OM)
        }
    }

    @Test
    fun mtn() {
        assertThrows<InternalErrorException> {
            provider.get(GatewayType.MTN)
        }
    }

    @Test
    fun authorizeNet() {
        assertThrows<InternalErrorException> {
            provider.get(GatewayType.AUTHORIZE_NET)
        }
    }

    @Test
    fun paypal() {
        assertThrows<InternalErrorException> {
            provider.get(GatewayType.PAYPAL)
        }
    }

    @Test
    fun stripe() {
        assertThrows<InternalErrorException> {
            provider.get(GatewayType.STRIPE)
        }
    }
}
