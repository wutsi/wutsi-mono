package com.wutsi.platform.payment

import com.wutsi.platform.payment.provider.flutterwave.spring.FlutterwaveConfiguration
import com.wutsi.platform.payment.provider.mtn.spring.MTNConfiguration
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(
    value = [
        MTNConfiguration::class,
        FlutterwaveConfiguration::class,
    ],
)
annotation class EnableWutsiPayment
