package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.GetCategoryResponse
import com.wutsi.membership.access.service.CategoryService
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class GetCategoryDelegate(
    private val service: CategoryService,
    private val request: HttpServletRequest,
) {
    fun invoke(id: Long): GetCategoryResponse {
        val category = service.findById(id)
        val language = request.getHeader("Accept-Language")
        return GetCategoryResponse(
            category = service.toCategory(category, language),
        )
    }
}
