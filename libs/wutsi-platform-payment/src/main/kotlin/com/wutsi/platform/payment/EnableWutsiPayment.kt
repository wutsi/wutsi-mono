package com.wutsi.platform.payment

import com.wutsi.platform.payment.provider.flutterwave.spring.FlutterwaveConfiguration
import com.wutsi.platform.payment.provider.mtn.spring.MTNConfiguration
import com.wutsi.platform.payment.provider.paypal.spring.PaypalConfiguration
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(
    value = [
        MTNConfiguration::class,
        FlutterwaveConfiguration::class,
        PaypalConfiguration::class,
    ],
)
annotation class EnableWutsiPayment
