package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.SaveCategoryRequest
import com.wutsi.membership.access.service.CategoryService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
public class SaveCategoryDelegate(
    private val service: CategoryService,
    private val httpRequest: HttpServletRequest,
    private val logger: KVLogger,
) {
    public fun invoke(id: Long, request: SaveCategoryRequest) {
        logger.add("request_title", request.title)

        val language = httpRequest.getHeader("Accept-Language")
        service.save(id, request, language)
    }
}
