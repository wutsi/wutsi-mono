package com.wutsi.blog.app.page.admin.store

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

abstract class AbstractStoreController(
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    protected fun checkAccess(){
        requestContext.currentStore() ?:
        throw NotFoundException(
            error = Error(
                code = ErrorCode.STORE_NOT_FOUND
            )
        )
    }
}
