package com.wutsi.blog.app.backend

import com.wutsi.blog.comment.dto.SearchCommentRequest
import com.wutsi.blog.comment.dto.SearchCommentResponse
import com.wutsi.blog.transaction.dto.CreateWalletCommand
import com.wutsi.blog.transaction.dto.CreateWalletResponse
import com.wutsi.blog.transaction.dto.GetWalletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WalletBackend(
    private val rest: RestTemplate,
) {
    @Value("\${wutsi.application.backend.wallet.endpoint}")
    private lateinit var endpoint: String

    fun get(id: String): GetWalletResponse =
        rest.getForEntity("$endpoint/$id", GetWalletResponse::class.java).body!!

    fun create(cmd: CreateWalletCommand): CreateWalletResponse =
        rest.postForEntity("$endpoint/commands/create", cmd, CreateWalletResponse::class.java).body!!

    fun search(request: SearchCommentRequest): SearchCommentResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchCommentResponse::class.java).body!!
}
