package com.wutsi.blog.config

import com.nhaarman.mockitokotlin2.mock
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreatePaymentResponse
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.CreateTransferResponse
import com.wutsi.platform.payment.model.GetPaymentResponse
import com.wutsi.platform.payment.model.GetTransferResponse
import com.wutsi.platform.payment.model.Party
import com.wutsi.platform.payment.provider.flutterwave.FWGateway
import java.util.UUID

open class TestFWGateway : FWGateway(
    http = mock(),
    secretKey = "",
    testMode = false,
    encryptor = mock(),
) {
    override fun getTransfer(transactionId: String) = GetTransferResponse(
        payee = Party(),
        amount = Money(10000.0, "XAF"),
        externalId = transactionId,
        status = Status.PENDING,
        fees = Money(10.0, "XAF"),
        financialTransactionId = UUID.randomUUID().toString(),
    )

    override fun getPayment(transactionId: String) = GetPaymentResponse(
        payer = Party(),
        amount = Money(10000.0, "XAF"),
        externalId = transactionId,
        status = Status.PENDING,
        fees = Money(10.0, "XAF"),
        financialTransactionId = UUID.randomUUID().toString(),
    )

    override fun createPayment(request: CreatePaymentRequest) = CreatePaymentResponse(
        status = Status.PENDING,
        transactionId = UUID.randomUUID().toString(),
    )

    override fun createTransfer(request: CreateTransferRequest) = CreateTransferResponse(
        status = Status.PENDING,
        transactionId = UUID.randomUUID().toString(),
    )
}
