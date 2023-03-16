package com.wutsi.platform.payment

import com.wutsi.platform.payment.provider.flutterwave.spring.FlutterwaveConfiguration
import com.wutsi.platform.payment.provider.mtn.spring.MTNConfiguration
import com.wutsi.platform.payment.spring.GatewayConfiguration
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(
    value = [
        GatewayConfiguration::class,
        MTNConfiguration::class,
        FlutterwaveConfiguration::class,
    ],
)
annotation class EnableWutsiPayment
