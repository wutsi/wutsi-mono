package com.wutsi.checkout.manager.delegate

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.PaymentMethodSummary
import com.wutsi.checkout.manager.dto.PaymentProviderSummary
import com.wutsi.checkout.manager.dto.SearchPaymentMethodRequest
import com.wutsi.checkout.manager.dto.SearchPaymentMethodResponse
import com.wutsi.checkout.manager.util.SecurityUtil
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class SearchPaymentMethodDelegate(
    private val logger: KVLogger,
    private val checkoutAccessApi: CheckoutAccessApi,
) {
    public fun invoke(request: SearchPaymentMethodRequest): SearchPaymentMethodResponse {
        logger.add("request_status", request.status)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val paymentMethods = checkoutAccessApi.searchPaymentMethod(
            request = com.wutsi.checkout.access.dto.SearchPaymentMethodRequest(
                accountId = SecurityUtil.getAccountId(),
                status = request.status,
                limit = request.limit,
                offset = request.offset,
            ),
        ).paymentMethods
        return SearchPaymentMethodResponse(
            paymentMethods = paymentMethods.map {
                PaymentMethodSummary(
                    accountId = it.accountId,
                    token = it.token,
                    type = it.type,
                    number = it.number,
                    status = it.status,
                    created = it.created,
                    provider = PaymentProviderSummary(
                        id = it.provider.id,
                        name = it.provider.name,
                        logoUrl = it.provider.logoUrl,
                        code = it.provider.code,
                        type = it.provider.type,
                    ),
                )
            },
        )
    }
}
