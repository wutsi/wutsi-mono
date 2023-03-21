package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.GetCategoryResponse
import com.wutsi.marketplace.access.service.CategoryService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class GetCategoryDelegate(
    private val service: CategoryService,
    private val request: HttpServletRequest,
    private val logger: KVLogger,
) {
    fun invoke(id: Long): GetCategoryResponse {
        val language = request.getHeader("Accept-Language")
        logger.add("language", language)

        val category = service.findById(id)
        return GetCategoryResponse(
            category = service.toCategory(category, language),
        )
    }
}
