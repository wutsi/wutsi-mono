package com.wutsi.platform.payment

import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreatePaymentResponse
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.CreateTransferResponse
import com.wutsi.platform.payment.model.GetPaymentResponse
import com.wutsi.platform.payment.model.GetTransferResponse
import java.io.IOException

interface Gateway {
    fun getType(): GatewayType

    @Throws(PaymentException::class, IOException::class)
    fun createPayment(request: CreatePaymentRequest): CreatePaymentResponse

    @Throws(PaymentException::class, IOException::class)
    fun getPayment(transactionId: String): GetPaymentResponse

    @Throws(PaymentException::class, IOException::class)
    fun createTransfer(request: CreateTransferRequest): CreateTransferResponse

    @Throws(PaymentException::class, IOException::class)
    fun getTransfer(transactionId: String): GetTransferResponse
}
