package com.wutsi.platform.payment.provider.mtn.product

import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.provider.mtn.model.MTNRequestToPayRequest
import com.wutsi.platform.payment.provider.mtn.model.MTNRequestToPayResponse

open class Collection(
    config: ProductConfig,
    http: Http,
) : AbstractProduct(config, http) {
    fun requestToPay(
        referenceId: String,
        accessToken: String,
        request: MTNRequestToPayRequest,
    ) {
        http.post(
            referenceId = referenceId,
            uri = uri("v1_0/requesttopay"),
            requestPayload = request,
            headers = headers(referenceId, accessToken),
            responseType = Any::class.java,
        )
    }

    fun requestToPay(referenceId: String, accessToken: String) =
        http.get(
            referenceId = referenceId,
            uri = uri("v1_0/requesttopay/$referenceId"),
            headers = headers(null, accessToken),
            responseType = MTNRequestToPayResponse::class.java,
        )!!

    override fun uri(path: String): String =
        config.environment.baseUrl + "/collection/$path"
}
