package com.wutsi.platform.payment.provider.mtn.product

import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.provider.mtn.model.MTNTransferRequest
import com.wutsi.platform.payment.provider.mtn.model.MTNTransferResponse

open class Disbursement(
    config: ProductConfig,
    http: Http,
) : AbstractProduct(config, http) {
    fun transfer(
        referenceId: String,
        accessToken: String,
        request: MTNTransferRequest,
    ) {
        http.post(
            referenceId = referenceId,
            uri = uri("v1_0/transfer"),
            requestPayload = request,
            headers = headers(referenceId, accessToken),
            responseType = Any::class.java,
        )
    }

    fun transfer(referenceId: String, accessToken: String) =
        http.get(
            referenceId = referenceId,
            uri = uri("v1_0/transfer/$referenceId"),
            headers = headers(referenceId, accessToken),
            responseType = MTNTransferResponse::class.java,
        )!!

    override fun uri(path: String): String =
        config.environment.baseUrl + "/disbursement/$path"
}
