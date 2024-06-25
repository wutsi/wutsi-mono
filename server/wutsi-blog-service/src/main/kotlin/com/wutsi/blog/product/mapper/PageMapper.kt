package com.wutsi.blog.product.mapper

import com.wutsi.blog.product.domain.PageEntity
import com.wutsi.blog.product.dto.Page
import org.springframework.stereotype.Service

@Service
class PageMapper {
    fun toPage(page: PageEntity) = Page(
        id = page.id ?: -1,
        productId = page.product.id ?: -1,
        contentType = page.contentType,
        contentUrl = page.contentUrl,
        number = page.number
    )
}
