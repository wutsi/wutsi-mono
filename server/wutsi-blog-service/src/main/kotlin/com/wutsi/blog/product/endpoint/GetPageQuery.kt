package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.GetPageResponse
import com.wutsi.blog.product.mapper.PageMapper
import com.wutsi.blog.product.service.PageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetPageQuery(
    private val service: PageService,
    private val mapper: PageMapper,
) {
    @GetMapping("/v1/products/{productId}/pages/{number}")
    fun execute(@PathVariable productId: Long, @PathVariable number: Int): GetPageResponse {
        val page = service.find(productId, number)
        return GetPageResponse(
            page = mapper.toPage(page)
        )
    }
}
