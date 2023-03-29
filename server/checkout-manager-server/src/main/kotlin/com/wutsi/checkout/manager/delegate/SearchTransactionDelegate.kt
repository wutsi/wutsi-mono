package com.wutsi.checkout.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.SearchTransactionRequest
import com.wutsi.checkout.manager.dto.SearchTransactionResponse
import com.wutsi.checkout.manager.dto.TransactionSummary
import org.springframework.stereotype.Service

@Service
public class SearchTransactionDelegate(
    private val checkoutAccessApi: CheckoutAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(request: SearchTransactionRequest): SearchTransactionResponse {
        val transactions = checkoutAccessApi.searchTransaction(
            request = com.wutsi.checkout.access.dto.SearchTransactionRequest(
                customerAccountId = request.customerAccountId,
                businessId = request.businessId,
                type = request.type,
                orderId = request.orderId,
                status = request.status,
                limit = request.limit,
                offset = request.offset,
            ),
        ).transactions
        return SearchTransactionResponse(
            transactions = transactions.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    TransactionSummary::class.java,
                )
            },
        )
    }
}
