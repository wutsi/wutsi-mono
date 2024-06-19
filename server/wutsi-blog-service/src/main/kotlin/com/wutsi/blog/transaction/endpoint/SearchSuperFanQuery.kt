package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.SearchSuperFanRequest
import com.wutsi.blog.transaction.dto.SearchSuperFanResponse
import com.wutsi.blog.transaction.dto.SuperFanSummary
import com.wutsi.blog.transaction.service.SuperFanService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class SearchSuperFanQuery(
    private val service: SuperFanService,
) {
    @PostMapping("/v1/super-fans/queries/search")
    fun create(@RequestBody @Valid request: SearchSuperFanRequest): SearchSuperFanResponse {
        val txs = service.search(request)
        return SearchSuperFanResponse(
            superFans = txs.map { fan ->
                SuperFanSummary(
                    id = fan.id ?: "",
                    transactionCount = fan.transactionCount,
                    value = fan.value,
                    userId = fan.userId,
                    walletId = fan.walletId,
                )
            },
        )
    }
}
