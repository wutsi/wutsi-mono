package com.wutsi.checkout.manager.delegate

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.PaymentProviderSummary
import com.wutsi.checkout.manager.dto.SearchPaymentProviderRequest
import com.wutsi.checkout.manager.dto.SearchPaymentProviderResponse
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class SearchPaymentProviderDelegate(
    private val logger: KVLogger,
    private val checkoutAccessApi: CheckoutAccessApi,
) {
    public fun invoke(request: SearchPaymentProviderRequest): SearchPaymentProviderResponse {
        logger.add("request_country", request.country)
        logger.add("request_number", request.number)
        logger.add("request_type", request.type)

        val providers = checkoutAccessApi.searchPaymentProvider(
            request = com.wutsi.checkout.access.dto.SearchPaymentProviderRequest(
                number = request.number,
                country = request.country,
                type = request.type,
            ),
        ).paymentProviders
        return SearchPaymentProviderResponse(
            paymentProviders = providers.map {
                PaymentProviderSummary(
                    id = it.id,
                    name = it.name,
                    logoUrl = it.logoUrl,
                    code = it.code,
                    type = it.type,
                )
            },
        )
    }
}
