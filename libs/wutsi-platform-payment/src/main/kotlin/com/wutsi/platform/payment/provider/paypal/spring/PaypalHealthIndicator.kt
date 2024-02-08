package com.wutsi.platform.payment.provider.paypal.spring

import com.wutsi.platform.payment.provider.paypal.Paypal
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class PaypalHealthIndicator(
    private val gateway: Paypal,
) : HealthIndicator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaypalHealthIndicator::class.java)
    }

    override fun health(): Health {
        val now = System.currentTimeMillis()
        try {
            gateway.health()
            return Health.up()
                .withDetail("durationMillis", System.currentTimeMillis() - now)
                .build()
        } catch (ex: Exception) {
            LOGGER.error("Health failure", ex)
            return Health.down()
                .withDetail("durationMillis", System.currentTimeMillis() - now)
                .withException(ex)
                .build()
        }
    }
}
