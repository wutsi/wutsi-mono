package com.wutsi.checkout.manager.delegate

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.GetPaymentMethodResponse
import com.wutsi.checkout.manager.dto.PaymentMethod
import com.wutsi.checkout.manager.dto.PaymentProviderSummary
import org.springframework.stereotype.Service

@Service
public class GetPaymentMethodDelegate(private val checkoutAccessApi: CheckoutAccessApi) {
    public fun invoke(token: String): GetPaymentMethodResponse {
        val paymentMethod = checkoutAccessApi.getPaymentMethod(token).paymentMethod
        return GetPaymentMethodResponse(
            paymentMethod = PaymentMethod(
                accountId = paymentMethod.accountId,
                country = paymentMethod.country,
                updated = paymentMethod.updated,
                created = paymentMethod.created,
                status = paymentMethod.status,
                type = paymentMethod.type,
                number = paymentMethod.number,
                ownerName = paymentMethod.ownerName,
                token = token,
                deactivated = paymentMethod.deactivated,
                provider = PaymentProviderSummary(
                    id = paymentMethod.provider.id,
                    name = paymentMethod.provider.name,
                    logoUrl = paymentMethod.provider.logoUrl,
                    code = paymentMethod.provider.code,
                    type = paymentMethod.provider.type,
                ),
            ),
        )
    }
}
