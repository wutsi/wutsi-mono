package com.wutsi.blog.app.backend

import com.wutsi.blog.transaction.dto.GetTransactionResponse
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.SearchTransactionResponse
import com.wutsi.blog.transaction.dto.SubmitChargeCommand
import com.wutsi.blog.transaction.dto.SubmitChargeResponse
import com.wutsi.blog.transaction.dto.SubmitDonationCommand
import com.wutsi.blog.transaction.dto.SubmitDonationResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class TransactionBackend(
    private val rest: RestTemplate,
) {
    @Value("\${wutsi.application.backend.transaction.endpoint}")
    private lateinit var endpoint: String

    fun get(id: String, sync: Boolean = false): GetTransactionResponse =
        rest.getForEntity("$endpoint/$id?sync=$sync", GetTransactionResponse::class.java).body!!

    fun donate(command: SubmitDonationCommand): SubmitDonationResponse =
        rest.postForEntity("$endpoint/commands/submit-donation", command, SubmitDonationResponse::class.java).body!!

    fun charge(command: SubmitChargeCommand): SubmitChargeResponse =
        rest.postForEntity("$endpoint/commands/submit-charge", command, SubmitChargeResponse::class.java).body!!

    fun search(request: SearchTransactionRequest): SearchTransactionResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchTransactionResponse::class.java).body!!
}
