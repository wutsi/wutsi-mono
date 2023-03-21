package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.SearchCategoryRequest
import com.wutsi.membership.access.dto.SearchCategoryResponse
import com.wutsi.membership.access.service.CategoryService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class SearchCategoryDelegate(
    private val service: CategoryService,
    private val httpRequest: HttpServletRequest,
    private val logger: KVLogger,
) {
    fun invoke(request: SearchCategoryRequest): SearchCategoryResponse {
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val language = httpRequest.getHeader("Accept-Language")
        val categories = service.search(request, language)
        logger.add("count", categories.size)

        return SearchCategoryResponse(
            categories = categories.map { service.toCategorySummary(it, language) },
        )
    }
}
