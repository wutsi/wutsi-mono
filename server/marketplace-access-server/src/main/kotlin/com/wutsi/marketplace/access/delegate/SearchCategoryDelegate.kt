package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.SearchCategoryRequest
import com.wutsi.marketplace.access.dto.SearchCategoryResponse
import com.wutsi.marketplace.access.service.CategoryService
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
        logger.add("request_level", request.level)
        logger.add("request_keyword", request.keyword)
        logger.add("request_category_ids", request.categoryIds)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)
        logger.add("request_parent_id", request.parentId)

        val language = httpRequest.getHeader("Accept-Language")
        logger.add("language", language)

        val categories = service.search(request, language)
        logger.add("count", categories.size)
        return SearchCategoryResponse(
            categories = categories.map { service.toCategorySummary(it, language) }
                .sortedBy { it.title },
        )
    }
}
