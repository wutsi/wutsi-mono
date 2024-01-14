package com.wutsi.blog.transaction.service

import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.InternalErrorException
import com.wutsi.platform.payment.Gateway
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.provider.flutterwave.FWGateway
import org.springframework.stereotype.Service

@Service
class PaymentGatewayProvider(
    private val flutterwave: FWGateway,
    private val none: NoneGateway
) {
    fun get(type: PaymentMethodType): Gateway = when (type) {
        PaymentMethodType.MOBILE_MONEY -> flutterwave
        PaymentMethodType.NONE -> none
        else -> throw InternalErrorException(
            error = Error(
                code = "payment_method_not_supported",
                data = mapOf(
                    "payment-method-type" to type,
                ),
            ),
        )
    }

    fun get(type: GatewayType): Gateway = when (type) {
        GatewayType.FLUTTERWAVE -> flutterwave
        else -> throw InternalErrorException(
            error = Error(
                code = "gateway_not_supported",
                data = mapOf(
                    "gateway-type" to type,
                ),
            ),
        )
    }
}
